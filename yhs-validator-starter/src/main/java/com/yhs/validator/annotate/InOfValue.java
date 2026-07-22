package com.yhs.validator.annotate;

import com.yhs.validator.InOfValueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * @author 03952-yehuasheng
 * @version Id: InOfValue.java, v0.1 2024/9/12 14:48 yehuasheng Exp $
 */
@Documented
@Constraint(validatedBy = InOfValueValidator.class) // 标明哪个类校验
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface InOfValue {
    /**
     * 输出的内容
     *
     * @return String
     */
    String message() default "信息不匹配";

    /**
     * 匹配对象
     *
     * @return String[]
     */
    String[] matchTarget() default {};

    /**
     * 组
     *
     * @return Class[]
     */
    Class[] groups() default {};

    /**
     * Payload
     *
     * @return Class<? extends Payload>[]
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * 是否必填
     *
     * @return boolean
     */
    boolean required() default true;
}
