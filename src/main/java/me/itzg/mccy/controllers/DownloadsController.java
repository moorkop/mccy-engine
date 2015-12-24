package me.itzg.mccy.controllers;

import me.itzg.mccy.services.FileRepoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author Geoff Bourne
 * @since 12/23/2015
 */
@RestController
@RequestMapping("/api/downloads")
public class DownloadsController {

    @Autowired
    private FileRepoService fileRepoService;

    @RequestMapping("worlds/{name:.+\\.zip}")
    @ResponseBody
    public Resource downloadWorld(
            @PathVariable("name") String worldFilename) throws IOException {
        Resource worldResource = fileRepoService.load("worlds", worldFilename);

        return worldResource;
    }
}
