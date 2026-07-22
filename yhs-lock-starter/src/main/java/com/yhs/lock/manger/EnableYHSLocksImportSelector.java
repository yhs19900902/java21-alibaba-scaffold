package com.yhs.lock.manger;

import com.yhs.lock.annotate.EnableYHSLocks;
import com.yhs.lock.properties.RedisConnType;
import org.springframework.cloud.commons.util.SpringFactoryImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author 07664-linwei
 * @version Id: EnableYHSLocksImportSelector.java, v 0.1 2022/5/12 11:31 lw Exp $
 */
public class EnableYHSLocksImportSelector extends SpringFactoryImportSelector<EnableYHSLocks> {

    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        AnnotationAttributes attributes = AnnotationAttributes
                .fromMap(metadata.getAnnotationAttributes(getAnnotationClass().getName(), true));
        RedisConnType redisConnType = attributes.getEnum("redisConnType");

        Environment env = getEnvironment();
        if (env instanceof ConfigurableEnvironment) {
            ConfigurableEnvironment configEnv = (ConfigurableEnvironment) env;
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            map.put("yhs.redisson.type", redisConnType);
            MapPropertySource propertySource = new MapPropertySource("redissonProperties", map);
            configEnv.getPropertySources().addLast(propertySource);
        }
        List<String> importsList = new ArrayList<>();
        importsList.add("com.yhs.lock.RedisAutoConfiguration");
        return importsList.toArray(new String[0]);
    }

    @Override
    protected boolean isEnabled() {
        return true;
    }
}
