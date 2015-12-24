package me.itzg.mccy.controllers;

import me.itzg.mccy.config.MccySettings;
import me.itzg.mccy.model.ApplicationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Geoff Bourne
 * @since 12/22/2015
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private MccySettings mccySettings;

    @RequestMapping("info")
    public ApplicationInfo getAppInfo() {
        return ApplicationInfo.from(mccySettings);
    }
}
