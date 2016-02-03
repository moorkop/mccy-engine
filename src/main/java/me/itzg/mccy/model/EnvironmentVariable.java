package me.itzg.mccy.model;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares that a field corresponds to an environment variable of the container.
 *
 * @author Geoff Bourne
 * @since 0.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface EnvironmentVariable {
    /**
     *
     * @return the name of the associated environment variable
     */
    String value();
}
