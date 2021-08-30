package io.basc.framework.web.message.annotation;

import io.basc.framework.context.result.ResultFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 为了兼容{@link ResultFactory}
 * 
 * @author shuchaowen
 *
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface FactoryResult {
	public boolean value() default true;
}
