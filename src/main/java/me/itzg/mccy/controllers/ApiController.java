package me.itzg.mccy.controllers;

import me.itzg.mccy.config.MccySettings;
import me.itzg.mccy.model.ApplicationInfo;
import me.itzg.mccy.model.MinecraftVersions;
import me.itzg.mccy.services.VersionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @author Geoff Bourne
 * @since 12/22/2015
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private MccySettings mccySettings;

    @Autowired
    private VersionsService versionsService;

    @RequestMapping("info")
    public ApplicationInfo getAppInfo() {
        return ApplicationInfo.from(mccySettings);
    }

    @RequestMapping("versions/{type}")
    public List<String> getVersions(@PathVariable("type")MinecraftVersions.Type type) throws IOException {
        return versionsService.getVersions(type);
    }
}
