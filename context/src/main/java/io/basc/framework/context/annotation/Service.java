package io.basc.framework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.core.annotation.AliasFor;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Aop
public @interface Service {
	@AliasFor(annotation = Component.class)
	String value() default "";
}
