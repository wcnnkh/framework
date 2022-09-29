package io.basc.framework.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author shuchaowen
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Indexed
public @interface Service {
	/**
	 * 别名， 默认使用第一个实现的接口做为别名
	 * 
	 * @return
	 */
	public String[] value() default {};
}
