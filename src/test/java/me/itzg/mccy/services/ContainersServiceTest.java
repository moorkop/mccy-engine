package me.itzg.mccy.services;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.ProgressHandler;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.ContainerInfo;
import com.spotify.docker.client.messages.ContainerState;
import com.spotify.docker.client.messages.NetworkSettings;
import me.itzg.mccy.config.MccyAssetSettings;
import me.itzg.mccy.config.MccySettings;
import me.itzg.mccy.model.ContainerDetails;
import me.itzg.mccy.model.ContainerRequest;
import me.itzg.mccy.types.MccyConstants;
import me.itzg.mccy.types.MccyException;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.Validator;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.ValidatorFactory;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

/**
 * @author Geoff Bourne
 * @since 1/8/2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration
public class ContainersServiceTest {

    @Configuration
    public static class Config {
        @Bean
        public ContainersService containersService() {
            return new ContainersService();
        }

        @Bean
        @Qualifier("mock")
        public DockerClientProxy dockerClientProxy() {
            final DockerClientProxy dockerClientProxy = mock(DockerClientProxy.class);

            try {
                when(dockerClientProxy.access(Mockito.any()))
                        .then(invocation ->
                                invocation.getArgumentAt(0, DockerClientConsumer.class).use(dockerClient()));
            } catch (DockerException | InterruptedException e) {
                throw new IllegalStateException(e);
            }

            return dockerClientProxy;
        }

        @Bean
        public MccySettings mccySettings() {
            final MccySettings mccySettings = new MccySettings();
            mccySettings.setDockerHostUri("http://localhost:2375");
            return mccySettings;
        }

        @Bean
        public MccyAssetSettings mccyAssetSettings() {
            final MccyAssetSettings mccyAssetSettings = new MccyAssetSettings();
            mccyAssetSettings.setVia(null);
            return mccyAssetSettings;
        }

        @Bean
        public String ourContainerId() {
            return "container-1";
        }

        @Bean
        @Qualifier("mock")
        public MetadataConversionService metadataConversionService() {
            return mock(MetadataConversionService.class);
        }

        @Bean
        @Qualifier("mock")
        public DockerClient dockerClient() {
            return mock(DockerClient.class);
        }

        @Bean
        @Qualifier("mock")
        public ServerStatusService serverStatusService() {
            return mock(ServerStatusService.class);
        }

        @Bean @Qualifier("mock")
        public ContainerBuilderService containerBuilderService() {
            return Mockito.mock(ContainerBuilderService.class);
        }
    }

    @Autowired
    private ContainersService containersService;

    @Autowired
    private DockerClient dockerClient;

    @Autowired
    private ContainerBuilderService containerBuilderService;

    @After
    public void tearDown() throws Exception {
        // needed since each unit test dirties this mock, which is a singleton bean
        reset(dockerClient);
    }

    @Test
    public void testScrubContainerName() throws Exception {
        assertEquals("It_s_the_best_", ContainersService.scrubContainerName("It's the best!"));
    }

    @Test
    public void testCreateContainer() throws Exception, MccyException {
        ContainerRequest request = new ContainerRequest();
        request.setName("server-1");
        request.setStartOnCreate(true);
        request.setAckEula(true);

        when(dockerClient.createContainer(any(ContainerConfig.class), anyString()))
                .thenReturn(new ContainerCreation("id-1"));

        ContainerConfig stubbedConfig = ContainerConfig.builder().build();
        when(containerBuilderService.buildContainerConfig(any(), any(), any()))
                .thenReturn(stubbedConfig);

        final UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost:8080");
        containersService.setProxyUriBuilder(uriComponentsBuilder);

        final String id = containersService.create(request, "user1",
                containerCreateStatus -> {});

        verify(dockerClient).pull(anyString(), any(ProgressHandler.class));

        final ArgumentCaptor<ContainerConfig> containerConfigCaptor =
                ArgumentCaptor.forClass(ContainerConfig.class);
        verify(dockerClient).createContainer(containerConfigCaptor.capture(), eq("server_1"));

        verify(dockerClient).startContainer(eq(id));

        Mockito.verifyNoMoreInteractions(dockerClient);

        assertThat(id, not(isEmptyOrNullString()));
        assertEquals(id, "id-1");
    }

    @Test
    public void testGetPrivateContainerAnon() throws Exception {

        ContainerInfo containerInfo = createContainerInfo("false", "user-2");

        when(dockerClient.inspectContainer("container-private"))
                .thenReturn(containerInfo);

        final ContainerDetails detailsOfPrivate = containersService.get("container-private", null);
        assertNull(detailsOfPrivate);
    }

    @Test
    public void testGetPublicContainerAnon() throws Exception {

        ContainerInfo containerInfo = createContainerInfo("true", "user-2");

        when(dockerClient.inspectContainer("container-private"))
                .thenReturn(containerInfo);

        final ContainerDetails detailsOfPrivate = containersService.get("container-private", null);
        assertNotNull(detailsOfPrivate);
    }

    @Test
    public void testGetPrivateContainerOwned() throws Exception {

        ContainerInfo containerInfo = createContainerInfo("false", "user-2");

        when(dockerClient.inspectContainer("container-private"))
                .thenReturn(containerInfo);

        final ContainerDetails detailsOfPrivate = containersService.get("container-private", "user-2");
        assertNotNull(detailsOfPrivate);
    }

    @Test
    public void testGetPrivateContainerNotOwned() throws Exception {

        ContainerInfo containerInfo = createContainerInfo("false", "user-2");

        when(dockerClient.inspectContainer("container-private"))
                .thenReturn(containerInfo);

        final ContainerDetails detailsOfPrivate = containersService.get("container-private", "user-other");
        assertNull(detailsOfPrivate);
    }

    private ContainerInfo createContainerInfo(String isPublic, String owner) {
        final Map<String, String> labels = new HashMap<>();
        labels.put(MccyConstants.MCCY_LABEL, "true");
        labels.put(MccyConstants.MCCY_LABEL_PUBLIC, isPublic);
        labels.put(MccyConstants.MCCY_LABEL_OWNER, owner);
        labels.put(MccyConstants.MCCY_LABEL_NAME, "container_name");

        ContainerInfo containerInfo = new ContainerInfo();
        setField(containerInfo, "config",
                ContainerConfig.builder()
                        .labels(labels)
                        .build());
        setField(containerInfo, "networkSettings",
                NetworkSettings.builder()
                        .build());
        setField(containerInfo, "state",
                new ContainerState());
        setField(containerInfo, "name", "container_name");
        return containerInfo;
    }
}