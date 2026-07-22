package com.yhs.encrypt;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author 07664-linwei
 * @version Id: EnableEncrypt.java, v 0.1 2022/7/4 14:35 lw Exp $
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(EncryptAutoConfiguration.class)
public @interface EnableEncrypt {
}
