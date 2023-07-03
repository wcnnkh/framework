package io.basc.framework.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.core.annotation.AliasFor;
import io.basc.framework.web.message.annotation.ResponseBody;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
@ResponseBody
public @interface RestController {
	@AliasFor(annotation = Controller.class)
	String value() default "";
}
