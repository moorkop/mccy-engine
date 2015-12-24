package me.itzg.mccy.controllers;

import me.itzg.mccy.config.MccySettings;
import me.itzg.mccy.model.SingleValue;
import me.itzg.mccy.services.FileRepoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

/**
 * @author Geoff Bourne
 * @since 12/23/2015
 */
@RestController
@RequestMapping("/api/uploads")
public class UploadsController {

    @Autowired
    private FileRepoService fileRepoService;

    @Autowired
    private MccySettings mccySettings;

    @RequestMapping(value = "worlds", method = RequestMethod.POST)
    public ResponseEntity<SingleValue<URI>> uploadWorld(@RequestParam("file")MultipartFile worldFile,
                                                   UriComponentsBuilder requestUri) throws IOException {

        final String filename = fileRepoService.save("worlds", ".zip", worldFile);

        final UriComponentsBuilder downloadsBuilder;
        if (mccySettings.getExternalUri() == null) {
            downloadsBuilder = requestUri;
        }
        else {
            downloadsBuilder = UriComponentsBuilder.fromUri(mccySettings.getExternalUri());
        }

        final URI downloadsUri = downloadsBuilder
                .path("/api/downloads/worlds/").path(filename).build().toUri();

        return ResponseEntity
                .created(downloadsUri)
                .body(SingleValue.of(downloadsUri));
    }
}
