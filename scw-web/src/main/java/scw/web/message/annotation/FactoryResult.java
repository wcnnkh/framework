package scw.web.message.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.context.result.ResultFactory;

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
