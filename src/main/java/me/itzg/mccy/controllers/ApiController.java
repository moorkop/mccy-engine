package me.itzg.mccy.controllers;

import me.itzg.mccy.model.ApplicationInfo;
import me.itzg.mccy.model.ServerType;
import me.itzg.mccy.services.SettingsService;
import me.itzg.mccy.services.VersionsService;
import me.itzg.mccy.types.ComparableVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Geoff Bourne
 * @since 12/22/2015
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private VersionsService versionsService;

    @Autowired
    private SettingsService settingsService;

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public ApplicationInfo getAppInfo() {
        return settingsService.getAppInfo();
    }

    /**
     * Provides the server versions that apply to the given server type.
     * @param type
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/versions/{type}", method = RequestMethod.GET)
    public List<ComparableVersion> getVersions(@PathVariable("type")ServerType type) throws IOException {
        return versionsService.getVersions(type);
    }

    /**
     * Provides the application settings that apply to UI/clients
     * @return
     */
    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    public Map<String,Object> getUiSettings() {
        return settingsService.getUiSettings();
    }
}
