package me.itzg.mccy.config;

import me.itzg.mccy.types.FreemarkerVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Supplement Spring Boot's auto config of Freemarker.
 *
 * @author Geoff Bourne
 * @since 0.2
 */
@Configuration
public class FreemarkerConfig extends FreeMarkerAutoConfiguration.FreeMarkerWebConfiguration {
    private Map<String, Object> variableMap;

    @Override
    public FreeMarkerConfigurer freeMarkerConfigurer() {
        final FreeMarkerConfigurer freeMarkerConfigurer = super.freeMarkerConfigurer();

        freeMarkerConfigurer.setFreemarkerVariables(variableMap);

        return freeMarkerConfigurer;
    }

    @Autowired
    public void setAllVariables(FreemarkerVariable[] variables) {
        variableMap = new HashMap<>();

        Stream.of(variables)
                .forEach(fv -> variableMap.put(fv.getName(), fv.getValue()));
    }
}
