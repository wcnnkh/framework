package io.basc.framework.orm.annotation;

import io.basc.framework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {
	@AliasFor("name")
	String value() default "";
	
	@AliasFor("value")
	String name() default "";
}
