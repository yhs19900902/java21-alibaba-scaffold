package com.yhs.lock.annotate;

import com.yhs.lock.manger.EnableYHSLocksImportSelector;
import com.yhs.lock.properties.RedisConnType;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启分布式锁
 *
 * @author 07664-linwei
 * @version Id: EnableYHSLocks.java, v 0.1 2022/5/12 10:59 lw Exp $
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(EnableYHSLocksImportSelector.class)
public @interface EnableYHSLocks {

    /**
     * redis 连接方式
     *
     * @return
     */
    RedisConnType redisConnType() default RedisConnType.SINGLE;
}
