package me.itzg.mccy.services;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingInputStream;
import me.itzg.mccy.services.impl.ZipMiningServiceImpl;
import me.itzg.mccy.types.ZipMiningHandler;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
public class ZipMiningServiceTest {
    private ZipMiningService service;

    @Before
    public void setUp() throws Exception {
        service = new ZipMiningServiceImpl();
        ReflectionTestUtils.setField(service, "fileIdHash", Hashing.md5());
    }

    @Test
    public void testRawHashOfFile() throws Exception {
        final ClassPathResource worldResource = new ClassPathResource("NewWorld.zip");
        try (InputStream in = worldResource.getInputStream()) {
            final HashingInputStream hashingInputStream = new HashingInputStream(Hashing.md5(), in);
            final byte[] buffer = new byte[1024];
            while (hashingInputStream.read(buffer) != -1){}

            assertEquals("2a9eaf43128d05bbebbb9a0d4b9f8892", hashingInputStream.hash().toString());
        }

    }

    @Test
    public void testJustFileId() throws Exception {
        final ClassPathResource worldResource = new ClassPathResource("NewWorld.zip");
        final String id;
        try (InputStream in = worldResource.getInputStream()) {
            id = service.interrogate(in, Optional.empty());
        }

        assertEquals("2a9eaf43128d05bbebbb9a0d4b9f8892", id);
    }

    @Test
    public void testInterceptFiles() throws Exception {
        final ClassPathResource worldResource = new ClassPathResource("NewWorld.zip");
        final String id;
        CompletableFuture<String> hashOfLevelDat = new CompletableFuture<>();

        final String[] expectedDataDatFiles = new String[]{
                "Mineshaft.dat",
                "Temple.dat",
                "villages.dat",
                "villages_end.dat",
                "villages_nether.dat"
        };

        AtomicInteger dataDatCount = new AtomicInteger();

        try (InputStream worldIn = worldResource.getInputStream()) {

            id = service.interrogate(worldIn, ZipMiningHandler.listBuilder()
                    .add(".*/level.dat", ((path, in) -> {
                        final HashingInputStream hashLevelDatIn = new HashingInputStream(Hashing.md5(), in);
                        byte[] buffer = new byte[1024];
                        try {
                            while (hashLevelDatIn.read(buffer) != -1){}
                            hashOfLevelDat.complete(hashLevelDatIn.hash().toString());
                        } catch (IOException e) {
                            hashOfLevelDat.completeExceptionally(e);
                        }
                    }))
                    .add(".*/data/.*\\.dat", (path, in) -> {

                        final int pos = path.lastIndexOf("/");
                        assertThat(path.substring(pos + 1), Matchers.isIn(expectedDataDatFiles));

                        dataDatCount.incrementAndGet();
                    })
                    .build());

            assertTrue(hashOfLevelDat.isDone());
            assertEquals("7661c3e52e999aeb6b5593072d189be0", hashOfLevelDat.get());

            assertEquals(5L, dataDatCount.longValue());
        }
        assertEquals("2a9eaf43128d05bbebbb9a0d4b9f8892", id);
    }
}