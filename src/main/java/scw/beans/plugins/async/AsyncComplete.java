package scw.beans.plugins.async;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 此注解可以保证方法一定执行，但此方法一定返回空
 * @author shuchaowen
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface AsyncComplete {
	/**
	 * 重试间隔时间, 默认为一分钟
	 * @return
	 */
	public long delayMillis() default 60;
	
	public TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
