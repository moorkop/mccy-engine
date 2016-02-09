package me.itzg.mccy.types;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
public class MinecraftVersionDeserializer extends JsonDeserializer<ComparableVersion> {
    @Override
    public ComparableVersion deserialize(JsonParser jsonParser,
                              DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        final String rawText = jsonParser.getText();
        if (rawText.contains(".")) {
            return ComparableVersion.of(rawText);
        }
        else {
            return ComparableVersion.of(rawText, MccyConstants.SNAPSHOT_VER_PATTERN);
        }
    }
}
