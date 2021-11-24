package io.basc.framework.beans.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.context.annotation.Indexed;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Indexed
public @interface Bean {
	/**
	 * bean的id，默认是使用当前类名做为id
	 * @return
	 */
	public String value() default "";
	
	/**
	 * bean的别名
	 * @return
	 */
	public String[] names() default {};
}
