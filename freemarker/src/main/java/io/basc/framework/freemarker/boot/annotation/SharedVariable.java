package io.basc.framework.freemarker.boot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.context.annotation.Indexed;

/**
 * 标识这是一个freemarker方法
 * 
 * @author shuchaowen
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Indexed
public @interface SharedVariable {
	/**
	 * 默认使用简写类名
	 * 
	 * @see Class#getSimpleName()
	 * @return
	 */
	String value() default "";
}