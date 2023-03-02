package io.basc.framework.mvc.result;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.context.transaction.ResultFactory;

/**
 * 为了兼容{@link ResultFactory}
 * 
 * @author wcnnkh
 *
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface FactoryResult {
	public Class<? extends ResultFactory> value() default ResultFactory.class;

	public boolean enable() default true;
}
