package io.basc.framework.context.ioc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.context.ioc.ValueProcessor;
import io.basc.framework.context.ioc.support.SmartValueProcessor;

@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Value {
	String value();

	Class<? extends ValueProcessor> processor() default SmartValueProcessor.class;

	String charsetName() default "";

	boolean listener() default true;
}
