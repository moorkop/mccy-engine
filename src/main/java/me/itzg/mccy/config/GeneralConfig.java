package me.itzg.mccy.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import me.itzg.mccy.services.WebServerPortProvider;
import me.itzg.mccy.types.FreemarkerVariable;
import me.itzg.mccy.types.UUIDGenerator;
import me.itzg.mccy.types.YamlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Executors;

/**
 * @author Geoff Bourne
 * @since 12/30/2015
 */
@Configuration
public class GeneralConfig {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired(required = false)
    private EmbeddedWebApplicationContext embeddedWebApplicationContext;

    @Bean
    public HashFunction fileIdHash() {
        return Hashing.md5();
    }

    @Bean
    public HashFunction modPackIdHash() {
        return Hashing.md5();
    }

    @Bean
    public YamlMapper yamlMapper(JacksonProperties jacksonProperties) {
        final YAMLFactory yamlFactory = new YAMLFactory();
        final ObjectMapper objectMapper = new ObjectMapper(yamlFactory);

        // leverage the deserialization settings used for JSON lenient processing
        for (Map.Entry<DeserializationFeature, Boolean> entry : jacksonProperties.getDeserialization().entrySet()) {
            if (entry.getValue()) {
                objectMapper.enable(entry.getKey());
            }
            else {
                objectMapper.disable(entry.getKey());
            }
        }

        return new YamlMapper(objectMapper);
    }

    @Bean @Autowired
    public String ourContainerId(Environment env) throws UnknownHostException {
        return env.acceptsProfiles("docker") ?
                InetAddress.getLocalHost().getHostName() : null;
    }

    @SuppressWarnings({"Convert2Lambda", "Anonymous2MethodRef"})
    @Bean
    public Converter<String, URI> uriConverterFactory() {
        return new Converter<String, URI>() {
            @Override
            public URI convert(String s) {
                return URI.create(s);
            }
        };
    }

    @Bean
    @Autowired
    public ConfigurableConversionService conversionService(Converter[] converters) {
        final ConfigurableConversionService ours = new DefaultConversionService();

        for (Converter converter : converters) {
            ours.addConverter(converter);
        }

        return ours;
    }

    @Bean
    public ConcurrentTaskExecutor remoteInvocationExecutor() {
        final CustomizableThreadFactory threadFactory = new CustomizableThreadFactory("remoteInv-");

        return new ConcurrentTaskExecutor(Executors.newCachedThreadPool(threadFactory));
    }

    @Bean
    public UUIDGenerator uuidGenerator() {
        return UUID::randomUUID;
    }

    @Bean
    public FreemarkerVariable buildProperties(@Value("classpath:build.properties") Resource propsResource) throws IOException {
        final Properties allProps = new Properties();
        try (InputStream in = propsResource.getInputStream()) {
            allProps.load(in);
        }

        return new FreemarkerVariable("project", allProps);
    }
}
