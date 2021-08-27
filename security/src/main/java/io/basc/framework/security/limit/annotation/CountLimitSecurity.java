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
 * @author shuchaowen
 *
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CountLimitSecurity {
	public boolean enable() default true;
	
	/**
	 * 数量
	 * @return
	 */
	public long maxCount();

	/**
	 * 默认单位为分钟
	 * @return
	 */
	public long period() default 1;

	/**
	 * 时间单位
	 * @return
	 */
	public TimeUnit timeUnit() default TimeUnit.SECONDS;
	
	/**
	 * 是否把全部参数做为条件
	 * @return
	 */
	public boolean useAllParameters() default false;
	
	public Class<? extends TemporaryCounter> counter() default TemporaryCounter.class;
	
	public Class<? extends CountLimitFactory> factory() default CountLimitFactory.class;
}
