package me.itzg.mccy.controllers;

import me.itzg.mccy.config.MccySettings;
import me.itzg.mccy.services.FileStorageService;
import me.itzg.mccy.services.ModsService;
import me.itzg.mccy.types.MccyConstants;
import me.itzg.mccy.types.MccyNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

/**
 * @author Geoff Bourne
 * @since 12/23/2015
 */
@RestController
@RequestMapping("/api/downloads")
public class DownloadsController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ModsService modsService;

    @RequestMapping(value = "/worlds/{name:.+\\.zip}", method = RequestMethod.GET)
    @ResponseBody
    public Resource downloadWorld(
            @PathVariable("name") String worldFilename) throws IOException {
        Resource worldResource = fileStorageService.load(MccyConstants.CATEGORY_WORLDS, worldFilename);

        return worldResource;
    }

    @RequestMapping(value = "/modpacks/{id}", method = RequestMethod.GET)
    public void downloadModPack(@PathVariable("id") String modPackId,
                                HttpServletResponse response) throws IOException, MccyNotFoundException {
        response.setContentType("application/x-zip-compressed");
        modsService.buildModPack(modPackId, response.getOutputStream());
    }

    static URI buildDownloadUri(UriComponentsBuilder requestUri, String filename, MccySettings mccySettings,
                                String category) {
        final UriComponentsBuilder downloadsBuilder;
        if (mccySettings.getExternalUri() == null) {
            downloadsBuilder = requestUri;
        }
        else {
            downloadsBuilder = UriComponentsBuilder.fromUri(mccySettings.getExternalUri());
        }

        return downloadsBuilder
                .path("/api/downloads/" + category + "/").path(filename).build().toUri();
    }
}
