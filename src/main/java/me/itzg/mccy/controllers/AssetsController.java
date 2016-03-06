package me.itzg.mccy.controllers;

import me.itzg.mccy.model.Asset;
import me.itzg.mccy.model.AssetCategory;
import me.itzg.mccy.model.AssetObjectPurpose;
import me.itzg.mccy.services.assets.AssetRouterService;
import me.itzg.mccy.types.MccyInvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
@RestController
@RequestMapping({"/assets","/a"})
public class AssetsController {

    @Autowired
    private AssetRouterService assetRouterService;

    /**
     * Uploads a new asset file to the system for access later.
     *
     * @param assetFile the asset file content itself
     * @param category the type of asset this is intended to be
     * @param auth injected by Spring
     * @return an id of the asset
     */
    @RequestMapping(method = RequestMethod.POST)
    public String uploadAsset(@RequestParam("file") MultipartFile assetFile,
                            @RequestParam("category") AssetCategory category,
                            Authentication auth) throws IOException, MccyInvalidFormatException {

        return assetRouterService.upload(assetFile, category, auth);
    }

    @RequestMapping("/{category}")
    @ResponseBody
    public List<Asset> queryByCategory(@PathVariable("category")AssetCategory category) {
        return assetRouterService.queryByCategory(category);
    }

    @RequestMapping(value = "/{category}/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Resource download(@PathVariable("category")AssetCategory category,
                             @PathVariable("id")String assetId) throws FileNotFoundException {

        return assetRouterService.downloadObject(category, AssetObjectPurpose.SOURCE, assetId);
    }
}
