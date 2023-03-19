package io.basc.framework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.core.annotation.AliasFor;

/**
 * bean的定义
 * 
 * @author wcnnkh
 *
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface Bean {
	@AliasFor("name")
	String[] value() default {};

	@AliasFor("value")
	String[] name() default {};

	String[] initMethod() default {};

	String[] destroyMethod() default { "close", "shutdown" };
}
