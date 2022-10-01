package io.basc.framework.context.annotation;

import io.basc.framework.aop.MethodInterceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Proxy {
	public Class<? extends MethodInterceptor>[] value() default {};

	public String[] names() default {};
}
