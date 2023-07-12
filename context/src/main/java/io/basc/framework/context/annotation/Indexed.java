package io.basc.framework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.context.Context;

/**
 * 上下文扫描时会允许加入上下文
 * 
 * @author wcnnkh
 * @see Context#getContextClasses()
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Indexed {
	int value() default 0;
}