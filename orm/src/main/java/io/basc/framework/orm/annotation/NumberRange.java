package io.basc.framework.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 范围，默认是正整形的范围(0 ~ 2147483647)
 * 
 * @author wcnnkh
 *
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NumberRange {
	public double min() default 0;

	public double max() default Integer.MAX_VALUE;
}
