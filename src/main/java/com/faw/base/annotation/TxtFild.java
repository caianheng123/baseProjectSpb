package com.faw.base.annotation;

import java.lang.annotation.*;

/**
 * Created by JG on 2020/5/25.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TxtFild {
    String value() default "";
}
