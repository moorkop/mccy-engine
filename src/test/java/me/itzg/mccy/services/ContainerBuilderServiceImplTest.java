package me.itzg.mccy.services;

import com.spotify.docker.client.messages.ContainerConfig;
import me.itzg.mccy.config.MccyAssetSettings;
import me.itzg.mccy.config.MccySettings;
import me.itzg.mccy.model.AssetCategory;
import me.itzg.mccy.model.AssetRef;
import me.itzg.mccy.model.ContainerRequest;
import me.itzg.mccy.types.MccyConstants;
import me.itzg.mccy.types.MccyException;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
public class ContainerBuilderServiceImplTest {
    private static final String CONTAINER_ID = "container-01";
    private ContainerBuilderServiceImpl containerBuilderService;
    private MccySettings mccySettings;
    private MccyAssetSettings mccyAssetSettings;

    @Before
    public void setUp() throws Exception {
        containerBuilderService = new ContainerBuilderServiceImpl();

        mccySettings = new MccySettings();
        ReflectionTestUtils.setField(containerBuilderService, "mccySettings", mccySettings);

        mccyAssetSettings = new MccyAssetSettings();
        ReflectionTestUtils.setField(containerBuilderService, "mccyAssetSettings", mccyAssetSettings);

        ReflectionTestUtils.setField(containerBuilderService, "ourContainerId", CONTAINER_ID);

        WebServerPortProvider webServerPortProvider = () -> 9090;
        ReflectionTestUtils.setField(containerBuilderService, "webServerPortProvider", webServerPortProvider);

    }

    @Test
    public void testCreatePublicContainer() throws Exception, MccyException {
        ContainerRequest request = new ContainerRequest();
        request.setName("server-1");
        request.setVisibleToPublic(true);
        request.setStartOnCreate(true);
        request.setAckEula(true);

        final ContainerConfig containerConfig = containerBuilderService.buildContainerConfig(
                request, "user1", UriComponentsBuilder.fromUriString("http://localhost"));

        final Map<String, String> labels = containerConfig.labels();
        assertThat(labels, Matchers.hasEntry(MccyConstants.MCCY_LABEL_PUBLIC, "true"));
        assertThat(labels, Matchers.hasEntry(MccyConstants.MCCY_LABEL_OWNER, "user1"));

        assertThat(containerConfig.env(), contains("EULA=TRUE"));
    }

    @Test
    public void testWorldAssetViaLink() throws Exception, MccyException {
        ContainerRequest request = createRequestWithWorldAsset();

        mccyAssetSettings.setVia(MccyAssetSettings.Via.LINK);

        final ContainerConfig containerConfig = containerBuilderService.buildContainerConfig(
                request, "user1", UriComponentsBuilder.fromUriString("http://localhost"));

        assertThat(containerConfig.env(), contains(
                String.format("WORLD=http://%s:%d/a/WORLD/asset-1", MccyConstants.LINK_MCCY, 9090),
                "EULA=TRUE"
        ));
    }

    @Test
    public void testWorldAssetViaOverlay() throws Exception, MccyException {
        ContainerRequest request = createRequestWithWorldAsset();

        mccyAssetSettings.setVia(MccyAssetSettings.Via.OVERLAY);
        mccyAssetSettings.setOverlayNetwork("mccy-assets");
        mccyAssetSettings.setMyOverlayName("mccy-engine");

        final ContainerConfig containerConfig = containerBuilderService.buildContainerConfig(
                request, "user1", UriComponentsBuilder.fromUriString("http://localhost"));

        assertThat(containerConfig.env(), contains(
                String.format("WORLD=http://%s:%d/a/WORLD/asset-1", "mccy-engine", 9090),
                "EULA=TRUE"
        ));

        assertThat(containerConfig.hostConfig().networkMode(), equalTo("mccy-assets"));
    }

    @Test
    public void testWorldAssetViaFixedUri() throws Exception, MccyException {
        ContainerRequest request = createRequestWithWorldAsset();

        mccyAssetSettings.setVia(MccyAssetSettings.Via.FIXED_URI);
        mccyAssetSettings.setFixedUri(URI.create("https://proxy/app"));

        final ContainerConfig containerConfig = containerBuilderService.buildContainerConfig(
                request, "user1", UriComponentsBuilder.fromUriString("http://localhost"));

        assertThat(containerConfig.env(), contains(
                "WORLD=https://proxy/app/a/WORLD/asset-1",
                "EULA=TRUE"
        ));
    }

    @Test
    public void testWorldAssetViaRequestUri() throws Exception, MccyException {
        ContainerRequest request = createRequestWithWorldAsset();

        mccyAssetSettings.setVia(MccyAssetSettings.Via.REQUEST_URL);

        final ContainerConfig containerConfig = containerBuilderService.buildContainerConfig(
                request, "user1", UriComponentsBuilder.fromUriString("https://server"));

        assertThat(containerConfig.env(), contains(
                "WORLD=https://server/a/WORLD/asset-1",
                "EULA=TRUE"
        ));
    }

    private ContainerRequest createRequestWithWorldAsset() {
        ContainerRequest request = new ContainerRequest();
        request.setName("server-1");
        request.setVisibleToPublic(true);
        request.setStartOnCreate(true);
        request.setAckEula(true);
        request.setAssets(Collections.singletonList(
                new AssetRef().setCategory(AssetCategory.WORLD).setId("asset-1")));
        return request;
    }
    //TODO add tests for various asset link scenarios
    //fixed uri
    //request uri
}