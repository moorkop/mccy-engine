package me.itzg.mccy.controllers;

import me.itzg.mccy.config.MccySettings;
import me.itzg.mccy.model.UploadModsResponse;
import me.itzg.mccy.model.SingleValue;
import me.itzg.mccy.services.FileStorageService;
import me.itzg.mccy.services.ModsService;
import me.itzg.mccy.types.MccyConstants;
import me.itzg.mccy.types.MccyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

/**
 * @author Geoff Bourne
 * @since 12/23/2015
 */
@RestController
@RequestMapping("/api/uploads")
public class ApiUploadsController {
    private static Logger LOG = LoggerFactory.getLogger(ApiUploadsController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ModsService modsService;

    @Autowired
    private MccySettings mccySettings;

    @RequestMapping(value = "worlds", method = RequestMethod.POST)
    public ResponseEntity<SingleValue<URI>> uploadWorld(@RequestParam("file")MultipartFile worldFile,
                                                   UriComponentsBuilder requestUri) throws IOException {

        final String filename = fileStorageService.saveNeedsName(MccyConstants.CATEGORY_WORLDS,
                MccyConstants.EXT_WORLDS, true, worldFile);

        final URI downloadsUri = DownloadsController.buildDownloadUri(requestUri, filename, mccySettings, "worlds");

        return ResponseEntity
                .created(downloadsUri)
                .body(SingleValue.of(downloadsUri));
    }

    @RequestMapping(value = "mods", method = RequestMethod.POST)
    public UploadModsResponse uploadMods(@RequestPart("files") MultipartFile[] modFiles)
            throws IOException, MccyException {
        LOG.debug("Upload mods provided: {}", modFiles);
        UploadModsResponse response = new UploadModsResponse();

        for (MultipartFile modFile : modFiles) {
            try {
                response.getMods().add(modsService.ingest(modFile));
            } catch (MccyException | IOException e) {
                UploadModsResponse.Failed failed = new UploadModsResponse.Failed();
                failed.setReason(e.getMessage()+"("+e.getClass()+")");
                failed.setFilename(modFile.getOriginalFilename());

                response.addFailed(failed);

                LOG.warn("Failed to ingest {}", e, modFile);
            }
        }

        return response;
    }
}
