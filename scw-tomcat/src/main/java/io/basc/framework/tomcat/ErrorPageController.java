package io.basc.framework.tomcat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ErrorPageController {
	public int[] value();

	public String exceptionType() default "";

	public String charset() default "";
}
