package kafka.react.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import react.HibernateClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reactivestreams.Publisher;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class KafkaReader {
    private static final Logger logger = LogManager.getLogger(KafkaReader.class);
    private final ReceiverOptions<String, String> receiverOptions;
    private final HibernateClient hibernateClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public KafkaReader(HibernateClient hibernateClient) {

        String topic = System.getenv("KAFKA_TOPIC");
        String bootstrapServers = System.getenv("KAFKA_BOOTSTRAP_SERVERS");
        String consumerGroup = System.getenv("KAFKA_CONSUMER_GROUP");
        if (topic == null || topic.isEmpty()) {
            throw new IllegalStateException("KAFKA_TOPIC environment variable must be set");
        }

        if (bootstrapServers == null || bootstrapServers.isEmpty()) {
            throw new IllegalStateException("KAFKA_BOOTSTRAP_SERVERS environment variable must be set");
        }
        if (consumerGroup == null || consumerGroup.isEmpty()) {
            throw new IllegalStateException("KAFKA_CONSUMER_GROUP environment variable must be set");
        }
        this.hibernateClient = hibernateClient;
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        consumerProps.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        consumerProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        ReceiverOptions<String, String> receiverOptions = ReceiverOptions.create(consumerProps);
        this.receiverOptions = receiverOptions.subscription(Collections.singleton(topic));

    }

    public Disposable start() {

        var consume = Flux.defer(() -> KafkaReceiver.create(this.receiverOptions).receive()
                        .groupBy(ReceiverRecord::key)
                        .flatMap(group -> group.flatMap(this::processEvent))
                )
                .subscribe();

        Runtime.getRuntime()
                .addShutdownHook(
                        new Thread("kafka-consumer-shutdown-hook") {
                            @Override
                            public void run() {
                                consume.dispose();
                            }
                        });

        return consume;
    }

    private Publisher<Void> processEvent(ReceiverRecord<String, String> record) {
        long startTime = System.nanoTime();
        return Mono.fromCallable(() -> {


                    MetricsRegister.registerCounter("events_received_total", Map.of());


                    JsonNode jsonNode = mapper.readTree(record.value());
                    logger.info(String.format("Received message: thread=%s key=%s, value=%s, offset=%d%n",
                            Thread.currentThread().getName(), record.key(), jsonNode.toPrettyString(), record.offset()));


                    return TransactionRecord.getTransaction(jsonNode.get("key").asText(),
                            "kafkaReactor", jsonNode.get("index").asInt(), record.key(), record.offset(), Thread.currentThread().getName());
                }).flatMapMany(hibernateClient::persist)
                .doOnNext(unused -> {
                    record.receiverOffset().acknowledge();
                    long duration = System.nanoTime() - startTime;
                    MetricsRegister.registerTime("event_consume_duration_seconds", Duration.of(duration, ChronoUnit.SECONDS));
                });


    }
}
