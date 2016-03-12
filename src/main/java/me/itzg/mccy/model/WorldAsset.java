package me.itzg.mccy.model;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
public class WorldAsset extends Asset<WorldDescriptor> {

    private WorldDescriptor worldDetails;

    @Override
    public WorldDescriptor getDetails() {
        return worldDetails;
    }

    public WorldDescriptor getWorldDetails() {
        return worldDetails;
    }

    public void setWorldDetails(WorldDescriptor worldDetails) {
        this.worldDetails = worldDetails;
    }
}
