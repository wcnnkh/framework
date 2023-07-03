package io.basc.framework.web.message.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.context.annotation.Component;
import io.basc.framework.core.annotation.AliasFor;

/**
 * 显示指明controller使用的实例名称
 * 
 * @author wcnnkh
 *
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ResponseBody {
	@AliasFor(annotation = Component.class)
	String value() default "";
}
