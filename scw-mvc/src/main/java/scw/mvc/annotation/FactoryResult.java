package scw.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 为了兼容ResultFactory
 * @author shuchaowen
 *
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface FactoryResult {
	public Class<? extends scw.result.ResultFactory> value() default scw.result.ResultFactory.class;

	public boolean enable() default true;
}
