package io.basc.framework.security.limit.annotation;

import io.basc.framework.data.TemporaryCounter;
import io.basc.framework.security.limit.CountLimitFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 访问次数安全配置
 * 
 * @author wcnnkh
 *
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CountLimitSecurity {
	boolean enable() default true;

	long maxCount();

	long period() default 1;

	TimeUnit timeUnit() default TimeUnit.SECONDS;

	boolean useAllParameters() default false;

	Class<? extends TemporaryCounter> counter() default TemporaryCounter.class;

	Class<? extends CountLimitFactory> factory() default CountLimitFactory.class;
}
