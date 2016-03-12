package me.itzg.mccy.services.assets;

import me.itzg.mccy.model.AssetCategory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AssetConsumerSpec {
    AssetCategory category();
}
