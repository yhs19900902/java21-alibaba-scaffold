package com.yhs.validator.annotate;

import com.yhs.validator.PhoneValueValidator;
import com.yhs.validator.enums.PhoneRegion;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 校验手机号注解
 *
 * @author 07664-linwei
 * @version Id: PhoneValue.java, v 0.1 2022/4/23 9:17 lw Exp $
 */
@Documented
@Constraint(validatedBy = PhoneValueValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface PhoneValue {

    String message() default "手机号码格式不正确";

    Class[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 是否必填
     * <p>
     * 如果必填，在校验的时候本字段没值就会报错
     */
    boolean required() default true;

    /**
     * 手机号地区
     *
     * @return
     */
    PhoneRegion region() default PhoneRegion.INLAND;

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
