package io.basc.framework.rmi.beans;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.beans.factory.component.Aop;
import io.basc.framework.beans.factory.component.Component;
import io.basc.framework.core.annotation.AliasFor;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Component
@Aop(interceptors = RmiClientExecutionInterceptor.class)
public @interface RmiClient {
	@AliasFor("host")
	String value() default "";

	@AliasFor("value")
	String host() default "";
}
