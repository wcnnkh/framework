package io.basc.framework.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.CONSTRUCTOR,
		ElementType.TYPE_PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface KeyValuePair {
	public String key();

	public String value();
}
