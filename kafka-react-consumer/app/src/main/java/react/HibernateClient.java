package react;

import io.smallrye.mutiny.converters.uni.UniReactorConverters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.reactive.mutiny.Mutiny;
import org.reactivestreams.Publisher;

public class HibernateClient {
    private static final Logger logger = LogManager.getLogger(HibernateClient.class);

    private final Mutiny.SessionFactory factory;

    public HibernateClient() {
        factory = EntityMan.createEntityManager().unwrap(Mutiny.SessionFactory.class);
    }

    public static HibernateClient init() {
        return new HibernateClient();
    }

    public Publisher<Void> persist(Object object) {
        logger.info(object);
        return factory.withTransaction(session -> session.persist(object))
                .convert().with(UniReactorConverters.toFlux())
                ;
    }

    public Publisher<Object> merge(Object object) {
        return factory.withTransaction(session -> session.merge(object))
                .convert().with(UniReactorConverters.toFlux())
                ;
    }

    public Mutiny.SessionFactory getFactory() {
        return factory;
    }
}
