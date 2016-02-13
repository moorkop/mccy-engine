package me.itzg.mccy.config;

import me.itzg.mccy.types.FreemarkerVariable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
@Configuration
public class VersionsConfig {

    public static final String VERSION_SUFFIX = ".version";


    @Bean
    public FreemarkerVariable versions(@Value("classpath:build.properties") Resource propsResource) throws IOException {
        final Properties allProps = new Properties();
        try (InputStream in = propsResource.getInputStream()) {
            allProps.load(in);
        }

        final Properties versionProps = new Properties();

        final Enumeration<?> names = allProps.propertyNames();
        while (names.hasMoreElements()) {
            final String name = (String) names.nextElement();
            if (name.endsWith(VERSION_SUFFIX)) {
                versionProps.setProperty(name.substring(0, name.length() - VERSION_SUFFIX.length()),
                        allProps.getProperty(name));
            }
        }

        return new FreemarkerVariable("versions", versionProps);
    }
}
