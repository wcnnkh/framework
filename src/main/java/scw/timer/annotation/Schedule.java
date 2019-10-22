package scw.timer.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import scw.timer.Delayed;
import scw.timer.TaskListener;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Schedule {
	public String name();

	public Class<? extends Delayed> delay() default Delayed.class;

	/**
	 * 如果为-1就不坎周期执行
	 * @return
	 */
	public long period();

	/**
	 * 默认时间单位为：毫秒
	 * @return
	 */
	public TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

	public Class<? extends TaskListener> listener() default TaskListener.class;
}
