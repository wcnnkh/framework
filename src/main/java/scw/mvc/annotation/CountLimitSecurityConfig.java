package scw.mvc.annotation;

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
public @interface CountLimitSecurityConfig {
	public String name();
	
	public long maxCount();

	public long period();

	public TimeUnit timeUnit() default TimeUnit.HOURS;
}
