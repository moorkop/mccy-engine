package me.itzg.mccy.services.assets;

import me.itzg.mccy.model.AssetCategory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
    }

    @AssetConsumerSpec(category = AssetCategory.WORLD)
    public static class TestConsumer implements AssetConsumer {

        private MultipartFile captor;

        @Override
        public String consume(MultipartFile assetFile, Authentication auth) {
            captor = assetFile;
            return captor.getName();
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
    public void testUpload() throws Exception {

        MultipartFile multipartFile = new MockMultipartFile("world.zip", new byte[0]);
        final String id = service.upload(multipartFile, AssetCategory.WORLD, null);

        assertEquals("world.zip", id);
        assertNotNull(consumer.getCaptor());
        assertEquals("world.zip", consumer.getCaptor().getName());
    }
}