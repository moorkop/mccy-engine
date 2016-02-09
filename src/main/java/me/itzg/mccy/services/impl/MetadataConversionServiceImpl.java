package me.itzg.mccy.services.impl;

import me.itzg.mccy.model.EnvironmentVariable;
import me.itzg.mccy.services.MetadataConversionService;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Geoff Bourne
 * @since 0.1
 */
@Service
public class MetadataConversionServiceImpl implements MetadataConversionService {

    @Autowired
    private ConversionService conversionService;

    @Override
    public void fillFromEnv(List<String> env, Object target) {
        Map<String, String> envMap = extractEnvListToMap(env);

        final BeanWrapper propertyAccess = PropertyAccessorFactory.forBeanPropertyAccess(target);

        ReflectionUtils.doWithLocalFields(target.getClass(), f -> {
            final EnvironmentVariable[] annos = f.getAnnotationsByType(EnvironmentVariable.class);

            final Optional<String> evValue = Stream.of(annos)
                    .map(evAnno -> envMap.get(evAnno.value()))
                    .filter(val -> val != null)
                    .findFirst();

            if (evValue.isPresent()) {
                propertyAccess.setPropertyValue(f.getName(),
                        conversionService.convert(evValue.get(), f.getType()));
            }
        });

    }

    private static Map<String, String> extractEnvListToMap(List<String> env) {
        if (env == null) {
            return null;
        }

        return env.stream()
                .map(e -> e.split("=", 2))
                .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
    }

}
