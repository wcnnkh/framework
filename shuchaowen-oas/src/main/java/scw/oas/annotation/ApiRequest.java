package scw.oas.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ApiRequest {
	public String name();

	public String description() default "";

	public String contentType() default "";

	public ApiParam[] parameters() default {};
}
