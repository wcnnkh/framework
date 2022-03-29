package io.basc.framework.timer.boot.annotation;

import io.basc.framework.context.annotation.Indexed;
import io.basc.framework.timer.Delayed;
import io.basc.framework.timer.TaskListener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Indexed
public @interface Schedule {
	public String name();

	public Class<? extends Delayed> delay() default Delayed.class;

	/**
	 * 如果为-1就不看周期执行
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
