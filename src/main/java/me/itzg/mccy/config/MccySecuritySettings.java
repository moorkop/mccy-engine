package me.itzg.mccy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
@ConfigurationProperties("mccy.security")
@Component
public class MccySecuritySettings {

    private AllowAnonymous allowAnonymous = new AllowAnonymous();

    public AllowAnonymous getAllowAnonymous() {
        return allowAnonymous;
    }

    public void setAllowAnonymous(AllowAnonymous allowAnonymous) {
        this.allowAnonymous = allowAnonymous;
    }

    public static class AllowAnonymous {
        /**
         * Comma-separated list of paths that can be accessed anonymously via a GET
         */
        private List<String> get;

        public List<String> getGet() {
            return get;
        }

        public void setGet(List<String> get) {
            this.get = get;
        }
    }
}
