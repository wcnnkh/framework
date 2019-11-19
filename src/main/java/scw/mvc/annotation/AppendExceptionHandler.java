package scw.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.mvc.ExceptionHandler;

@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AppendExceptionHandler {
	public Class<? extends ExceptionHandler>[] value();
}
