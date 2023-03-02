package io.basc.framework.context.annotation;

import io.basc.framework.core.Ordered;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用类扫描实现Service Provider Interface
 * 
 * @author wcnnkh
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Indexed
public @interface Provider {
	Class<?>[] value() default {};

	boolean assignableValue() default true;

	Class<?>[] excludes() default {};

	int order() default Ordered.DEFAULT_PRECEDENCE;
}
