package scw.net.http.server.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestBean {
	public String value() default "";
}
