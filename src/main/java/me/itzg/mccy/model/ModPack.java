package me.itzg.mccy.model;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.util.MultiValueMap;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * A lightweight document is created for each combination of selected mod.
 *
 * @author Geoff Bourne
 * @since 1/1/2016
 */
@Document(indexName = "mccy")
public class ModPack {

    @Id @NotNull
    private String id;

    /**
     * Corresponds to the {@link com.fasterxml.jackson.annotation.JsonTypeInfo} of the {@link RegisteredMod} subclass
     */
    @NotNull @Size(min = 1)
    private String modType;

    @Size(min = 1)
    private Collection<String> mods;

    @Field(format = DateFormat.date_time)
    private Date lastAccess;

    public static String computeId(HashFunction hashFunction, Collection<String> modIds) {
        final Hasher hasher = hashFunction.newHasher();

        modIds.stream()
                .sorted()
                .forEachOrdered(id -> hasher.putString(id, StandardCharsets.UTF_8));

        return hasher.hash().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Collection<String> getMods() {
        return mods;
    }

    public void setMods(Collection<String> mods) {
        this.mods = mods;
    }

    public Date getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(Date lastAccess) {
        this.lastAccess = lastAccess;
    }

    public String getModType() {
        return modType;
    }

    public void setModType(String modType) {
        this.modType = modType;
    }
}