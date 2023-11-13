package react;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;
import jakarta.persistence.spi.ClassTransformer;
import jakarta.persistence.spi.PersistenceUnitInfo;
import jakarta.persistence.spi.PersistenceUnitTransactionType;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.reactive.provider.ReactivePersistenceProvider;
import org.hibernate.type.format.jackson.JacksonJsonFormatMapper;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class EntityMan {
    public static EntityManagerFactory createEntityManager() {

        ReactivePersistenceProvider reactivePersistenceProvider = new ReactivePersistenceProvider();

        String jdbcUrl = System.getenv("JDBC_URL_DATABASE");
        String databaseUser = System.getenv("DATABASE_USER");
        String databasePasswd = System.getenv("DATABASE_PASSWD");

        Map<String, Object> properties = new HashMap<>();
        properties.put(AvailableSettings.JAKARTA_JDBC_DRIVER , "org.postgresql.Driver");
        properties.put(AvailableSettings.JAKARTA_JDBC_URL, jdbcUrl);
        properties.put(AvailableSettings.JAKARTA_JDBC_USER, databaseUser);
        properties.put(AvailableSettings.JAKARTA_JDBC_PASSWORD, databasePasswd);
        properties.put("exclude-unlisted-classes",false);
        properties.put("hibernate.highlight_sql",true);
        properties.put("hibernate.format_sql",true);
        properties.put("hibernate.show_sql",true);
        properties.put(AvailableSettings.JSON_FORMAT_MAPPER,
                new JacksonJsonFormatMapper(JsonHibernate.mapper()));

        EntityManagerFactory entityManagerFactory = reactivePersistenceProvider
                .createContainerEntityManagerFactory(archiverPersistenceUnitInfo(),properties
                );

        return entityManagerFactory;

    }

    private static PersistenceUnitInfo archiverPersistenceUnitInfo() {
        return new PersistenceUnitInfo() {
            @Override
            public String getPersistenceUnitName() {
                return "ApplicationPersistenceUnit";
            }

            @Override
            public String getPersistenceProviderClassName() {
                return "org.hibernate.reactive.provider.ReactivePersistenceProvider";
            }

            @Override
            public PersistenceUnitTransactionType getTransactionType() {
                return PersistenceUnitTransactionType.RESOURCE_LOCAL;
            }

            @Override
            public DataSource getJtaDataSource() {
                return null;
            }

            @Override
            public DataSource getNonJtaDataSource() {
                return null;
            }

            @Override
            public List<String> getMappingFileNames() {
                return Collections.emptyList();
            }

            @Override
            public List<URL> getJarFileUrls() {
                try {
                    return Collections.list(this.getClass()
                            .getClassLoader()
                            .getResources(""));
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public URL getPersistenceUnitRootUrl() {
                return null;
            }

            @Override
            public List<String> getManagedClassNames() {
                return List.of();
            }

            @Override
            public boolean excludeUnlistedClasses() {
                return false;
            }

            @Override
            public SharedCacheMode getSharedCacheMode() {
                return null;
            }

            @Override
            public ValidationMode getValidationMode() {
                return null;
            }

            @Override
            public Properties getProperties() {
                return new Properties();
            }

            @Override
            public String getPersistenceXMLSchemaVersion() {
                return null;
            }

            @Override
            public ClassLoader getClassLoader() {
                return null;
            }

            @Override
            public void addTransformer(ClassTransformer transformer) {

            }

            @Override
            public ClassLoader getNewTempClassLoader() {
                return null;
            }
        };
    }
}
