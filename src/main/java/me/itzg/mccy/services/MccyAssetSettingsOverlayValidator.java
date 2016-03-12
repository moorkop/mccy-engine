package me.itzg.mccy.services;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import me.itzg.mccy.config.MccyAssetSettings;
import me.itzg.mccy.types.ValidOverlaySettings;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
@Component
public class MccyAssetSettingsOverlayValidator extends AbstractContraintsValidator<ValidOverlaySettings, MccyAssetSettings> {

    public static final String MESSAGE = "is required for assets via overlay";

    public MccyAssetSettingsOverlayValidator() {
        addValidation((mccyAssetSettings, violationsHolder) -> {
            if (Strings.isNullOrEmpty(mccyAssetSettings.getMyOverlayName())) {
                return violationsHolder.addPropertyViolation(MESSAGE, "myOverlayName");
            }
            else {
                return true;
            }
        });

        addValidation((mccyAssetSettings, violationsHolder) ->
                !Strings.isNullOrEmpty(mccyAssetSettings.getOverlayNetwork()) ||
                        violationsHolder.addPropertyViolation(MESSAGE, "overlayNetwork"));
    }

    @Override
    protected boolean applicable(MccyAssetSettings obj) {
        return obj.getVia() == MccyAssetSettings.Via.OVERLAY;
    }
}
