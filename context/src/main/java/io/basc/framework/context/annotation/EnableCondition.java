package io.basc.framework.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableCondition {
	
	String value() default "true";
	
	/**
	 * 满足条件就表示可用
	 * @return
	 */
	String condition();
}
