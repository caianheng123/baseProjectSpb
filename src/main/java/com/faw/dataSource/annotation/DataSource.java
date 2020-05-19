package com.faw.dataSource.annotation;

import java.lang.annotation.*;

/**
 * 多数据源注解
 * @author liushengbin
 * @email liushengbin7@gmail.com
 * @date 2018/9/16 22:16
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {
    String name() default "";
}
