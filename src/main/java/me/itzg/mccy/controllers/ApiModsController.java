package me.itzg.mccy.controllers;

import me.itzg.mccy.config.MccySettings;
import me.itzg.mccy.model.RegisteredMod;
import me.itzg.mccy.model.RegisteredModReference;
import me.itzg.mccy.model.ServerType;
import me.itzg.mccy.model.SingleValue;
import me.itzg.mccy.services.ModsService;
import me.itzg.mccy.types.MccyConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author Geoff Bourne
 * @since 12/31/2015
 */
@RestController
@RequestMapping("/api")
public class ApiModsController {

    @Autowired
    private ModsService modsService;

    @Autowired
    private MccySettings mccySettings;

    @RequestMapping(value = "/mods", method = RequestMethod.GET)
    public List<? extends RegisteredMod> get(@RequestParam("mcversion")Optional<String> byVersion) {
        if (byVersion.isPresent()) {
            return modsService.queryByMinecraftVersion(byVersion.get());
        }
        else {
            return modsService.queryAll();
        }
    }

    @RequestMapping(value = "/mods/_suggest", method = RequestMethod.GET)
    public List<? extends RegisteredMod> suggestWithinVersion(@RequestParam("mcversion") String minecraftVersion,
                                                              @RequestParam("type") ServerType type,
                                                              @RequestParam("input") String autoCompleteInput) {
        return modsService.querySuggestions(minecraftVersion, type, autoCompleteInput);
    }

    @RequestMapping(value = "/mods/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") String id) {
        modsService.delete(id);
    }

    @RequestMapping(value = "/mods/{id}", method = RequestMethod.POST)
    public void save(@PathVariable("id") String id,
                     @RequestBody RegisteredMod mod) {
        modsService.save(id, mod);
    }

    @RequestMapping(value = "/modpacks", method = RequestMethod.POST)
    public ResponseEntity<SingleValue<URI>> registerModPack(@Validated @RequestBody Collection<RegisteredModReference> modRefs,
                                                            UriComponentsBuilder requestUri) {

        final String modPackId = modsService.registerModPack(modRefs);

        final URI downloadsUri = DownloadsController.buildDownloadUri(requestUri, modPackId+ MccyConstants.EXT_MOD_PACK, mccySettings, "modpacks");

        return ResponseEntity.created(downloadsUri)
                .body(SingleValue.of(downloadsUri));
    }
}
