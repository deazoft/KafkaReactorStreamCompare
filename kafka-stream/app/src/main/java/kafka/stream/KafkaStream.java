package kafka.stream;

import kafka.stream.react.HibernateClient;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;

import java.util.Properties;

public class KafkaStream {
    private final Properties props;
    private final String topic;
    private final String outputTopic;
    private final HibernateClient hibernateClient;
    public KafkaStream(HibernateClient hibernateClient){
        topic = System.getenv("KAFKA_TOPIC");
        outputTopic = System.getenv("KAFKA_OUTPUT_TOPIC");
        String bootstrapServers = System.getenv("KAFKA_BOOTSTRAP_SERVERS");
        String streamGroup = System.getenv("KAFKA_STREAM_GROUP");

        props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, streamGroup);
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
//        props.put(StreamsConfig.TOPOLOGY_OPTIMIZATION_CONFIG, StreamsConfig.OPTIMIZE);
//        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 10);
//        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);
        this.hibernateClient = hibernateClient;

    }
    public void start() {
        Topology topology = new Topology();
        topology.addSource("Source", topic)
                .addProcessor("Process", new MyProcessorSupplier(hibernateClient), "Source")
                .addSink("Sink", outputTopic, "Process");
        // Build the topology
        KafkaStreams streams = new KafkaStreams(topology, props);
        streams.start();

        // Add shutdown hook for graceful closure
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
