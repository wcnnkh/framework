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
public @interface ResultFactory {
	public Class<? extends scw.util.result.ResultFactory> value() default scw.util.result.ResultFactory.class;

	public boolean enable() default true;
}
