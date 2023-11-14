package kafka.stream.react;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class JsonHibernate {
    private static final ObjectMapper DEFAULT_SERIALIZER;

    public JsonHibernate() {
    }

    public static ObjectMapper mapper() {
        return DEFAULT_SERIALIZER;
    }

    static {
        ObjectMapper defaultObjectMapper = new ObjectMapper();
        defaultObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        defaultObjectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        defaultObjectMapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
        defaultObjectMapper.registerModule(new JavaTimeModule());
        defaultObjectMapper.registerModule(new Jdk8Module());
        defaultObjectMapper.registerModule(new GuavaModule());
        defaultObjectMapper.enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);
        DEFAULT_SERIALIZER = defaultObjectMapper;
    }
}
