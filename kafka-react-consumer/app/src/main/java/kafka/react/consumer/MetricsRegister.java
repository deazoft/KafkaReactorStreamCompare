package kafka.react.consumer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;
public interface MetricsRegister {
    MeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

    static String getPrometheusScrape() {
        StringBuilder response = new StringBuilder();
        if (registry instanceof PrometheusMeterRegistry) {
            PrometheusMeterRegistry prometheusRegistry = (PrometheusMeterRegistry) registry;
            response.append(prometheusRegistry.scrape());
        }
        return response.toString();
    }

    static void registerTime(String name, Duration duration) {
        Timer timer =
                Timer.builder(name)
                        .publishPercentiles(0.5, 0.95, 0.99)
                        .publishPercentileHistogram()
                        .register(registry);
        timer.record(duration);
    }

    static void registerTimeWithSLA(
            String name, Duration duration, Duration sla, Map<String, String> tags) {
        Timer timer =
                Timer.builder(name)
                        .publishPercentiles(0.5, 0.75, 0.95, 0.99)
                        .sla(sla)
                        .publishPercentileHistogram()
                        .tags(
                                tags.entrySet().stream()
                                        .map(e -> Tag.of(e.getKey(), e.getValue()))
                                        .collect(Collectors.toList()))
                        .register(registry);
        timer.record(duration);
    }

    static void registerCounter(String name, Map<String, String> tags) {
        Counter.builder(name)
                .tags(
                        tags.entrySet().stream()
                                .map(e -> Tag.of(e.getKey(), e.getValue()))
                                .collect(Collectors.toList()))
                .register(registry)
                .increment();
    }
}
