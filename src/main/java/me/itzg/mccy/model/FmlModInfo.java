package me.itzg.mccy.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Derived from https://github.com/MinecraftForge/FML/wiki/FML-mod-information-file
 *
 * @author Geoff Bourne
 * @since 12/29/2015
 */
public class FmlModInfo {
    private int modListVersion;

    private List<FmlModListEntry> modList;

    public FmlModInfo() {
    }

    public FmlModInfo(ArrayList<FmlModListEntry> entries) {
        this.modList = entries;
    }

    public int getModListVersion() {
        return modListVersion;
    }

    public void setModListVersion(int modListVersion) {
        this.modListVersion = modListVersion;
    }

    public List<FmlModListEntry> getModList() {
        return modList;
    }

    public void setModList(List<FmlModListEntry> modList) {
        this.modList = modList;
    }

}
