package me.itzg.mccy.config;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Geoff Bourne
 * @since 12/30/2015
 */
@Configuration
public class GeneralConfig {

    @Bean
    public HashFunction fileIdHash() {
        return Hashing.md5();
    }

    @Bean
    public HashFunction modPackIdHash() {
        return Hashing.md5();
    }
}
