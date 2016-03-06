package me.itzg.mccy.services.assets;

import me.itzg.mccy.types.MccyInvalidFormatException;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
public interface AssetConsumer {
    String consume(MultipartFile assetFile, Authentication auth) throws IOException, MccyInvalidFormatException;
}
