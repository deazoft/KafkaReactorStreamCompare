package annotations;

import org.hibernate.type.descriptor.jdbc.JdbcType;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Inherited
@Retention(RUNTIME)
public @interface JdbcTypeCode {
    /**
     * The standard {@linkplain java.sql.Types JDBC Types} code or a custom code.
     * This ultimately decides which {@link JdbcType} is used to "understand" the
     * described SQL data type.
     */
    int value();
}
