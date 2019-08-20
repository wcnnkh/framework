package scw.servlet.parameter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 以json方式解析
 * @author shuchaowen
 *
 */
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Json {
	/**
	 * 是否是最顶层对象
	 * @return
	 */
	public boolean value() default false;
}
