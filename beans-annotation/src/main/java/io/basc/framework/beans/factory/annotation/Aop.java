package io.basc.framework.beans.factory.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.execution.aop.ExecutionInterceptor;

/**
 * 是否启用aop
 * 
 * @author wcnnkh
 *
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Aop {
	Class<?>[] interfaces() default {};

	Class<? extends ExecutionInterceptor>[] interceptors() default {};

	String[] interceptorNames() default {};
}
