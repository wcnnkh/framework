package io.basc.framework.web.pattern.annotation;

import io.basc.framework.context.annotation.Indexed;
import io.basc.framework.http.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Indexed
public @interface RequestMapping {
	String value() default "";

	HttpMethod[] methods() default {};

	String[] consumes() default {};

	String[] produces() default {};
}
