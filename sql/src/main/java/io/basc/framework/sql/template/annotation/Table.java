package io.basc.framework.sql.template.annotation;

import io.basc.framework.orm.annotation.Entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Entity
public @interface Table {
	String name() default "";

	String engine() default "";

	String charset() default "";

	String rowFormat() default "";

	boolean create() default true;

	String comment() default "";
}
