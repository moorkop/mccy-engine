package me.itzg.mccy.types;

import me.itzg.mccy.services.MccyAssetSettingsFixedUriValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used as a constraint on {@link me.itzg.mccy.config.MccyAssetSettings} to trigger
 * mutual validation of {@link me.itzg.mccy.config.MccyAssetSettings.Via#NETWORK} and
 * its required fields.
 *
 * @author Geoff Bourne
 * @since 0.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Constraint(validatedBy = MccyAssetSettingsFixedUriValidator.class)
public @interface ValidViaFixedUriSettings {
    String message() default "invalid fixedUri settings";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
