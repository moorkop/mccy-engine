package me.itzg.mccy.services;

import me.itzg.mccy.config.MccyAssetSettings;
import me.itzg.mccy.types.ValidViaFixedUriSettings;
import org.springframework.stereotype.Component;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
@Component
public class MccyAssetSettingsFixedUriValidator
        extends AbstractContraintsValidator<ValidViaFixedUriSettings, MccyAssetSettings> {

    public static final String MESSAGE = "is required for assets via fixedUri";

    @Override
    public void initialize(ValidViaFixedUriSettings annotation) {
        super.initialize(annotation);
        addValidation((mccyAssetSettings, violationsHolder) -> {
            return mccyAssetSettings.getFixedUri() != null ||
                    violationsHolder.addPropertyViolation(MESSAGE, "fixedUri");
        });
    }

    @Override
    protected boolean applicable(MccyAssetSettings obj) {
        return obj.getVia() == MccyAssetSettings.Via.FIXED_URI;
    }
}
