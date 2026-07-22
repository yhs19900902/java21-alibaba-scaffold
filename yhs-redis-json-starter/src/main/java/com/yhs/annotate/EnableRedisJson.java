package com.yhs.annotate;

import com.yhs.RedisJsonAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author 07664-linwei
 * @version Id: EnableRedisJson.java, v 0.1 2022/8/30 11:40 lw Exp $
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(RedisJsonAutoConfiguration.class)
public @interface EnableRedisJson {
}
