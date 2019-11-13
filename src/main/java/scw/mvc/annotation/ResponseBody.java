package scw.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.mvc.support.ResultResponseBodyService;
import scw.mvc.support.ResponseBodyService;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseBody {
	public boolean value() default true;

	public Class<? extends ResponseBodyService> service() default ResultResponseBodyService.class;
}
