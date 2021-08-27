package io.basc.framework.mvc.annotation;

import io.basc.framework.context.result.ResultFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 为了兼容{@link ResultFactory}
 * @author shuchaowen
 *
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface FactoryResult {
	public Class<? extends io.basc.framework.context.result.ResultFactory> value() default io.basc.framework.context.result.ResultFactory.class;

	public boolean enable() default true;
}
