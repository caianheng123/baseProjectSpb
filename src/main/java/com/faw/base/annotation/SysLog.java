package com.faw.base.annotation;

import java.lang.annotation.*;

/**
 * 系统日志注解
 * 
 * @author liushengbin
 * @email liushengbin7@gmail.com
 * @date 2018年3月8日 上午10:19:56
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {

	String value() default "";
}
