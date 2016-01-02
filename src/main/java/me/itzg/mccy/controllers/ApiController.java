package me.itzg.mccy.controllers;

import me.itzg.mccy.model.ApplicationInfo;
import me.itzg.mccy.model.MinecraftVersions;
import me.itzg.mccy.services.SettingsService;
import me.itzg.mccy.services.VersionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collection;
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

    @RequestMapping("info")
    public ApplicationInfo getAppInfo() {
        return settingsService.getAppInfo();
    }

    @RequestMapping("versions/{type}")
    public Collection<String> getVersions(@PathVariable("type")MinecraftVersions.Type type) throws IOException {
        return versionsService.getVersions(type);
    }

    @RequestMapping("settings")
    public Map<String,Object> getUiSettings() {
        return settingsService.getUiSettings();
    }
}
