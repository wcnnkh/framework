package scw.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 默认为每分钟的调用次数限制
 * 
 * @author shuchaowen
 *
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CountLimitSecurityCount {
	public long value();

	/**
	 * 默认单位为分钟
	 * @return
	 */
	public long period() default 1;

	/**
	 * 时间单位
	 * @return
	 */
	public TimeUnit timeUnit() default TimeUnit.MINUTES;
}
