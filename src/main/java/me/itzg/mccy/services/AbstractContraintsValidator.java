package me.itzg.mccy.services;

import me.itzg.mccy.config.MccyAssetSettings;
import org.apache.commons.collections.ArrayStack;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * Simplifies the creation of JSR-303 (for example implemented by Hibernate) custom constraint validators.
 *
 * <p>
 *     To create a custom constraint, first create an annotation annotated with {@link javax.validation.Constraint}:
 *     <pre>
{@literal @}Retention(RetentionPolicy.RUNTIME)
{@literal @}Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
{@literal @}Constraint(validatedBy = YourImplOfValidator.class)
public @interface ValidViaFixedUriSettings {
    String message() default "invalid fixedUri settings";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
  *     </pre>
 * </p>
 * <p>
 *     And then extend this class:
 *     <pre>
{@literal @}Component
public class YourImplOfValidator extends AbstractContraintsValidator<ValidViaNetworkSettings, MccyAssetSettings> {

    public YourImplOfValidator() {
        addValidation((mccyAssetSettings, violationsHolder) ->
            !Strings.isNullOrEmpty(mccyAssetSettings.getNetwork()) ||
            violationsHolder.addPropertyViolation(MESSAGE, "overlayNetwork"));

    }
   {@literal @}Override
    protected boolean applicable(MccyAssetSettings obj) {
        return obj.getVia() == MccyAssetSettings.Via.NETWORK;
    }
}
 *     </pre>
 * </p>
 * @author Geoff Bourne
 * @since 0.2
 */
public abstract class AbstractContraintsValidator<A extends Annotation,O> implements ConstraintValidator<A, O> {

    private final List<BiFunction<O, ViolationsHolder, Boolean>> validations =
            new ArrayList<>();

    /**
     * Call this from within {@link #initialize(Annotation)}
     * @param validation
     */
    protected void addValidation(BiFunction<O, ViolationsHolder, Boolean> validation) {
        validations.add(validation);
    }

    @Override
    public void initialize(A annotation) {
    }

    public static class ViolationsHolder {
        private ConstraintValidatorContext constraintValidatorContext;

        private ViolationsHolder(ConstraintValidatorContext constraintValidatorContext) {
            this.constraintValidatorContext = constraintValidatorContext;
        }

        /**
         * @return always returns false as a convience for <code>validations</code> implementation
         */
        public boolean addPropertyViolation(String template, String propertyName) {
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate(template)
                    .addPropertyNode(propertyName).addConstraintViolation();

            return false;
        }
    }

    @Override
    public boolean isValid(O obj,
                           ConstraintValidatorContext constraintValidatorContext) {

        if (applicable(obj)) {
            boolean valid = true;
            constraintValidatorContext.disableDefaultConstraintViolation();
            final ViolationsHolder violationsHolder = new ViolationsHolder(constraintValidatorContext);

            for (BiFunction<O, ViolationsHolder, Boolean> validation : validations) {
                if (!validation.apply(obj, violationsHolder)) {
                    valid = false;
                }
            }

            return valid;
        }
        else {
            return true;
        }
    }

    protected abstract boolean applicable(O obj);
}
