package me.itzg.mccy.services;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;
import me.itzg.mccy.config.MccyAssetSettings;
import me.itzg.mccy.config.MccySettings;
import me.itzg.mccy.controllers.ApiAssetsController;
import me.itzg.mccy.model.AssetCategory;
import me.itzg.mccy.model.AssetRef;
import me.itzg.mccy.model.ContainerRequest;
import me.itzg.mccy.model.ServerType;
import me.itzg.mccy.types.MccyConstants;
import me.itzg.mccy.types.MccyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
@Service
public class ContainerBuilderServiceImpl implements ContainerBuilderService {

    private static final String WORLD_ZIP_NAME = "/world.zip";
    @Autowired
    private MccyAssetSettings mccyAssetSettings;

    @Autowired
    private MccySettings mccySettings;

    @Autowired
    private String ourContainerId;

    @Autowired
    private WebServerPortProvider webServerPortProvider;

    @Override
    public ContainerConfig buildContainerConfig(ContainerRequest request, String ownerUsername,
                                                UriComponentsBuilder requestUri)
            throws MccyException, DockerException, InterruptedException {
        final int requestedPort = request.getPort();
        PortBinding portBinding = PortBinding.of("",
                requestedPort != 0 ? String.valueOf(requestedPort) : "");
        Map<String, List<PortBinding>> portBindings =
                singletonMap(MccyConstants.SERVER_CONTAINER_PORT, singletonList(portBinding));

        final HostConfig.Builder hostConfig = HostConfig.builder()
                .portBindings(portBindings);

        final ArrayList<String> env = new ArrayList<>();

        fillAssetsLinkage(hostConfig, env, request, requestUri);

        return ContainerConfig.builder()
                .attachStdin(true)
                .tty(true)
                .exposedPorts(MccyConstants.SERVER_CONTAINER_PORT)
                .env(fillEnv(env, request))
                .image(mccySettings.getImage())
                .hostConfig(hostConfig.build())
                .labels(buildLabels(request, ownerUsername))
                .build();

    }

    protected Map<String, String> buildLabels(ContainerRequest request, String ownerUsername) {
        Map<String, String> labels = new HashMap<>();
        labels.put(MccyConstants.MCCY_LABEL, ContainersService.TRUE_VALUE);
        // store the name as provided by user
        labels.put(MccyConstants.MCCY_LABEL_NAME, request.getName());
        labels.put(MccyConstants.MCCY_LABEL_OWNER, ownerUsername);
        if (request.isVisibleToPublic()) {
            labels.put(MccyConstants.MCCY_LABEL_PUBLIC, ContainersService.TRUE_VALUE);
        }

        if (!Strings.isNullOrEmpty(request.getModpack())) {
            labels.put(MccyConstants.MCCY_LABEL_MODPACK_URL, request.getModpack());
        }

        return labels;
    }

    protected void addToEnv(ArrayList<String> env, String key, Object value) {
        if (value != null) {
            env.add(String.format("%s=%s", key, value.toString()));
        }
    }

    protected List<String> fillEnv(ArrayList<String> env, ContainerRequest request) {
        if (request.isAckEula()) {
            addToEnv(env, "EULA", "TRUE");
        }

        fillStringInEnv(env, request.getVersion(), MccyConstants.ENV_VERSION);
        fillStringInEnv(env, request.getIcon(), MccyConstants.ENV_ICON);

        if (ourContainerId != null && mccySettings.isUsingLinkForContent()) {
            fillLinkedUriInEnv(env, request.getModpack(), MccyConstants.ENV_MODPACK);
        } else {
            fillStringInEnv(env, request.getModpack(), MccyConstants.ENV_MODPACK);
        }

        final ServerType type = request.getType();
        if (type != null) {
            addToEnv(env, "TYPE", type.name());
        }

        fillPlayerList(env, request.getWhitelist(), "WHITELIST");
        fillPlayerList(env, request.getOps(), "OPS");

        return env;
    }

    private int getOurPort() {
        return webServerPortProvider.getPort();
    }

    private void fillStringInEnv(ArrayList<String> env, String value, String envKey) {
        if (!Strings.isNullOrEmpty(value)) {
            addToEnv(env, envKey, value);
        }
    }

    private void fillPlayerList(ArrayList<String> env, List<String> playerList, String envKey) {
        if (playerList != null && !playerList.isEmpty()) {
            addToEnv(env, envKey, Joiner.on(",").join(playerList));
        }
    }

    private void fillLinkedUriInEnv(ArrayList<String> env, String originalUri, String varName) {
        if (originalUri == null) {
            return;
        }

        final String viaLink = UriComponentsBuilder.fromHttpUrl(originalUri)
                .host(MccyConstants.LINK_MCCY)
                .port(getOurPort())
                .build().toUriString();
        fillStringInEnv(env, viaLink, varName);
    }

    private void fillAssetsLinkage(HostConfig.Builder hostConfig, ArrayList<String> env,
                                   ContainerRequest request, UriComponentsBuilder requestUri) {
        boolean builtLink = false;
        if (ourContainerId != null && needsLink(request)) {
            hostConfig.links(ourContainerId + ":" + MccyConstants.LINK_MCCY);
            builtLink = true;
        }

        final List<AssetRef> assets = request.getAssets();
        if (assets == null || assets.isEmpty()) {
            return;
        }

        final Optional<AssetRef> worldAssetRef = assets.stream()
                .filter(assetRef -> assetRef.getCategory() == AssetCategory.WORLD)
                .findFirst();

        if (worldAssetRef.isPresent()) {
            final UriComponents builtUri;
            switch (mccyAssetSettings.getVia()) {
                case NETWORK:
                    builtUri = UriComponentsBuilder.newInstance()
                            .scheme("http")
                            .host(mccyAssetSettings.getMyNameOnNetwork())
                            .port(getOurPort())
                            .path(ApiAssetsController.ASSET_DOWNLOAD_PATH)
                            .path(WORLD_ZIP_NAME)
                            .build();
                    hostConfig.networkMode(mccyAssetSettings.getNetwork());
                    break;

                case FIXED_URI:
                    builtUri = UriComponentsBuilder
                            .fromUri(mccyAssetSettings.getFixedUri())
                            .path(ApiAssetsController.ASSET_DOWNLOAD_PATH)
                            .path(WORLD_ZIP_NAME)
                            .build();
                    break;

                case LINK:
                    builtUri = UriComponentsBuilder.newInstance()
                            .scheme("http")
                            .host(MccyConstants.LINK_MCCY)
                            .port(getOurPort())
                            .path(ApiAssetsController.ASSET_DOWNLOAD_PATH)
                            .path(WORLD_ZIP_NAME)
                            .build();
                    if (!builtLink) {
                        hostConfig.links(ourContainerId + ":" + MccyConstants.LINK_MCCY);
                    }
                    break;

                default:
                case REQUEST_URL:
                    builtUri = ((UriComponentsBuilder) requestUri.clone())
                            .replacePath(ApiAssetsController.ASSET_DOWNLOAD_PATH)
                            .path(WORLD_ZIP_NAME)
                            .build();
                    break;
            }

            env.add(MccyConstants.ENV_WORLD + "=" +
                    builtUri.expand(AssetCategory.WORLD, worldAssetRef.get().getId()));
        }
    }

    private boolean needsLink(ContainerRequest request) {
        return mccySettings.isUsingLinkForContent() &&
                (request.getModpack() != null);
    }

}
