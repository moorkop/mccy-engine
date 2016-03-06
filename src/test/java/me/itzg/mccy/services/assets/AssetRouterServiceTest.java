package me.itzg.mccy.services.assets;

import me.itzg.mccy.model.Asset;
import me.itzg.mccy.model.AssetCategory;
import me.itzg.mccy.model.WorldAsset;
import me.itzg.mccy.repos.AssetRepo;
import me.itzg.mccy.types.MccyInvalidFormatException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.Assert.*;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration
public class AssetRouterServiceTest {

    @Configuration
    public static class Context {
        @Bean
        public AssetRouterService assetRouterService() {
            return new AssetRouterService();
        }

        @Bean
        public TestConsumer consumer() {
            return new TestConsumer();
        }

        @Bean @Qualifier("mock")
        public AssetObjectService assetObjectService() {
            return Mockito.mock(AssetObjectService.class);
        }

        @Bean @Qualifier("mock")
        public AssetRepo assetRepo() {
            return Mockito.mock(AssetRepo.class);
        }
    }

    @AssetConsumerSpec(category = AssetCategory.WORLD)
    public static class TestConsumer implements AssetConsumer {

        private MultipartFile captor;

        @Override
        public Asset consume(MultipartFile assetFile, Authentication auth) {
            captor = assetFile;
            final WorldAsset worldAsset = new WorldAsset();
            worldAsset.setId("id-"+assetFile.getName());
            return worldAsset;
        }

        public MultipartFile getCaptor() {
            return captor;
        }
    }

    @Autowired
    private AssetRouterService service;

    @Autowired
    private TestConsumer consumer;


    @Test
    public void testUpload() throws Exception, MccyInvalidFormatException {

        MultipartFile multipartFile = new MockMultipartFile("world.zip", new byte[0]);
        final Asset asset = service.upload(multipartFile, AssetCategory.WORLD, null);

        assertEquals("id-world.zip", asset.getId());
        assertNotNull(consumer.getCaptor());
        assertEquals("world.zip", consumer.getCaptor().getName());
    }
}