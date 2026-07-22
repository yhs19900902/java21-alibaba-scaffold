package com.yhs.springdoc;


import com.yhs.base.factory.YamlPropertySourceFactory;
import com.yhs.springdoc.config.SpringDocAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import java.lang.annotation.*;

/**
 * @author 07664-linwei
 * @version Id: EnableSpringDoc.java, v 0.1 2022/4/30 14:50 lw Exp $
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({SpringDocAutoConfiguration.class, YHSApiDocAutoConfiguration.class})
@PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:yhs-doc.yml")
public @interface EnableSpringDoc {
}
