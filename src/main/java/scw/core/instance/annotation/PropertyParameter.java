package scw.core.instance.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 是否是属性参数
 * @author shuchaowen
 *
 */
@Target({ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyParameter {
	public boolean value() default true;
}
