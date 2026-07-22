package com.yhs.job.config.annotate;

import com.yhs.job.config.XxlJobAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author 07664-linwei
 * @version Id: EnableJob.java, v 0.1 2022/4/27 14:39 lw Exp $
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({XxlJobAutoConfiguration.class})
public @interface EnableJob {
}
