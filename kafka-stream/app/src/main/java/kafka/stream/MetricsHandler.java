package kafka.stream;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class MetricsHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext ctx) {
        ctx.response().setStatusCode(200).end(MetricsRegister.getPrometheusScrape());
    }
}
