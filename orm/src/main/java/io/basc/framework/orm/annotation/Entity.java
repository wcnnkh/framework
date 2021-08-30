package io.basc.framework.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.annotation.AliasFor;

@Target({ ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {
	@AliasFor("name")
	String value() default "";
	
	@AliasFor("value")
	String name() default "";
}
