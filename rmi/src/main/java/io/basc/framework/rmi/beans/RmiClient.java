package io.basc.framework.rmi.beans;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.context.annotation.Component;
import io.basc.framework.context.annotation.EnableAop;
import io.basc.framework.core.annotation.AliasFor;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Component
@EnableAop(interceptors = RmiClientExecutionInterceptor.class)
public @interface RmiClient {
	@AliasFor("host")
	String value() default "";

	@AliasFor("value")
	String host() default "";
}
