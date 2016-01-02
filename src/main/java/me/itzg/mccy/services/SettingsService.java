package me.itzg.mccy.services;

import me.itzg.mccy.config.MccySettings;
import me.itzg.mccy.model.ApplicationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Geoff Bourne
 * @since 12/30/2015
 */
@Service
public class SettingsService {
    @Autowired
    private MccySettings mccySettings;

    @Autowired
    private Environment env;

    public ApplicationInfo getAppInfo() {
        return ApplicationInfo.from(mccySettings);
    }

    public Map<String, Object> getUiSettings() {
        final String[] visible = mccySettings.getUiVisibleSettings();
        if (visible == null) {
            return null;
        }

        Map<String, Object> values = new HashMap<>(visible.length);
        for (String key : visible) {
            values.put(key, env.getProperty(key));
        }

        return values;
    }
}
