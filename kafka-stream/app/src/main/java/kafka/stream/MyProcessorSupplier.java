package kafka.stream;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.AbstractScheduledService;
import kafka.stream.react.HibernateClient;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class MyProcessorSupplier implements ProcessorSupplier<String, String> {
    private static final Logger logger = LogManager.getLogger(MyProcessorSupplier.class);
    private final HibernateClient hibernateClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public MyProcessorSupplier(HibernateClient hibernateClient) {
        this.hibernateClient = hibernateClient;
    }

    @Override
    public Processor<String, String> get() {
        return new Processor<String, String>() {
            private ProcessorContext context;

            @Override
            public void init(ProcessorContext context) {
                this.context = context;
            }

            @Override
            public void process(String key, String value) {
                long offset = context.offset();
                 Flux.from(processEvent(value, key,offset))
                         .collectList()
                         .subscribeOn(Schedulers.boundedElastic())
                         .block();

                // Forward the record to the sink topic
                //context.forward(key, value);
            }

            @Override
            public void close() {}
        };
    }

    private Publisher<Void> processEvent(String value, String key, Long offset) {
        long startTime = System.nanoTime();
        return Mono.fromCallable(() -> {
                    MetricsRegister.registerCounter("events_received_total", Map.of());


                    JsonNode jsonNode = mapper.readTree(value);
                    logger.info(String.format("Received message: thread=%s key=%s, value=%s, offset=%d%n",
                            Thread.currentThread().getName(), key, jsonNode.toPrettyString(), offset));


                    return TransactionRecord.getTransaction(jsonNode.get("key").asText(),
                            "kafkaReactor", jsonNode.get("index").asInt(), key, offset, Thread.currentThread().getName());
                }).flatMapMany(hibernateClient::persist)
                .doOnNext(unused -> {
                    long duration = System.nanoTime() - startTime;
                    MetricsRegister.registerTime("event_consume_duration_seconds", Duration.of(duration, ChronoUnit.SECONDS));
                });


    }
}
