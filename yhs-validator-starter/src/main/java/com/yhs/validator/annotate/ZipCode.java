package com.yhs.validator.annotate;

import com.yhs.validator.ZipCodeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 邮政编码校验 兼容港澳台
 *
 * @author 07664-linwei
 * @version Id: ZipCode.java, v 0.1 2022/4/23 9:35 lw Exp $
 */
@Documented
@Constraint(validatedBy = ZipCodeValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface ZipCode {


    String message() default "邮政编码格式不正确";

    Class[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 是否必填
     * <p>
     * 如果必填，在校验的时候本字段没值就会报错
     */
    boolean required() default true;

    /**
     * 同一个元素上指定多个该注解时使用
     */
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        PhoneValue[] value();
    }
}
