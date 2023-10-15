package io.basc.framework.feign.context;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.context.annotation.Component;
import io.basc.framework.context.annotation.Aop;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Component
@Aop(interceptors = FeginMethodExecutionInterceptor.class)
public @interface FeignClient {
	/**
	 * 默认不指明host，会去从配置文件中去查找feign.host
	 * 
	 * @return
	 */
	public String host() default "";
}
