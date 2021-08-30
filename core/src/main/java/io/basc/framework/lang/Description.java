package io.basc.framework.lang;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述
 * 
 * @author shuchaowen
 *
 */
@Target({ ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER,
		ElementType.PACKAGE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Description {
	public String value();
}