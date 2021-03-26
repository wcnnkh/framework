package scw.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.context.result.ResultFactory;

/**
 * 为了兼容{@link ResultFactory}
 * @author shuchaowen
 *
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface FactoryResult {
	public Class<? extends scw.context.result.ResultFactory> value() default scw.context.result.ResultFactory.class;

	public boolean enable() default true;
}
