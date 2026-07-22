package com.yhs.validator.annotate;

import com.yhs.validator.InOfEnumValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author S006538-zhuxiaoxiao
 * @version Id: InOfEnum.java, v 0.1 2021/8/24 14:16 zhuxiaoxiao Exp $
 */
@Documented
@Constraint(validatedBy = InOfEnumValidator.class) // 标明哪个类校验
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface InOfEnum {


    String message() default "属性枚举不匹配";

    Class[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends Enum> sourceEnum();

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
