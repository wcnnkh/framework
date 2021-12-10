package io.basc.framework.beans.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.beans.ioc.value.SmartValueProcessor;
import io.basc.framework.beans.ioc.value.ValueProcessor;

@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Value {
	String value();

	Class<? extends ValueProcessor> processor() default SmartValueProcessor.class;

	String charsetName() default "";

	/**
	 * 是否监听变更
	 * 
	 * @return
	 */
	boolean listener() default true;
}
