package me.itzg.mccy.model;

import java.util.List;

/**
 * @author Geoff Bourne
 * @since 12/30/2015
 */
public class FmlModListEntry {
    public static final FmlModListEntry EMPTY = new FmlModListEntry();
    private String modid;
    private String name;
    private String description;
    private String version;
    private String mcversion;
    private String url;
    private String updateUrl;
    private List<String> authorList;
    private String credits;
    private String logoFile;
    private List<String> screenshots;
    private String parent;
    private List<String> requiredMods;
    private List<String> dependencies;
    private List<String> dependants;
    private boolean useDependencyInformation;

    public String getModid() {
        return modid;
    }

    public void setModid(String modid) {
        this.modid = modid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    public List<String> getAuthorList() {
        return authorList;
    }

    public void setAuthorList(List<String> authorList) {
        this.authorList = authorList;
    }

    public void setAuthors(List<String> authorList) {
        this.authorList = authorList;
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public String getLogoFile() {
        return logoFile;
    }

    public void setLogoFile(String logoFile) {
        this.logoFile = logoFile;
    }

    public List<String> getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(List<String> screenshots) {
        this.screenshots = screenshots;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public List<String> getRequiredMods() {
        return requiredMods;
    }

    public void setRequiredMods(List<String> requiredMods) {
        this.requiredMods = requiredMods;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    public List<String> getDependants() {
        return dependants;
    }

    public void setDependants(List<String> dependants) {
        this.dependants = dependants;
    }

    public boolean isUseDependencyInformation() {
        return useDependencyInformation;
    }

    public void setUseDependencyInformation(boolean useDependencyInformation) {
        this.useDependencyInformation = useDependencyInformation;
    }

    public String getMcversion() {
        return mcversion;
    }

    public void setMcversion(String mcversion) {
        this.mcversion = mcversion;
    }
}
