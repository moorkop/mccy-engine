package me.itzg.mccy.services;

import com.google.common.base.Strings;
import me.itzg.mccy.config.MccyAssetSettings;
import me.itzg.mccy.types.ValidViaNetworkSettings;
import org.springframework.stereotype.Component;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
@Component
public class MccyAssetSettingsNetworkValidator extends AbstractContraintsValidator<ValidViaNetworkSettings, MccyAssetSettings> {

    public static final String MESSAGE = "is required for assets via overlay";

    public MccyAssetSettingsNetworkValidator() {
        addValidation((mccyAssetSettings, violationsHolder) -> {
            if (Strings.isNullOrEmpty(mccyAssetSettings.getMyNameOnNetwork())) {
                return violationsHolder.addPropertyViolation(MESSAGE, "myNameOnNetwork");
            }
            else {
                return true;
            }
        });

        addValidation((mccyAssetSettings, violationsHolder) ->
                !Strings.isNullOrEmpty(mccyAssetSettings.getNetwork()) ||
                        violationsHolder.addPropertyViolation(MESSAGE, "network"));
    }

    @Override
    protected boolean applicable(MccyAssetSettings obj) {
        return obj.getVia() == MccyAssetSettings.Via.NETWORK;
    }
}
