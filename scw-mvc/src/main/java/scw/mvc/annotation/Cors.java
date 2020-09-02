package scw.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.http.HttpMethod;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Cors {
	boolean enable() default true;

	String origin() default "*";

	String[] headers() default {};

	HttpMethod[] methods() default {};

	boolean credentials() default true;

	int maxAge() default -1;
}