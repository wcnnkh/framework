package io.basc.framework.timer.annotation;

import io.basc.framework.timer.TaskListener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Crontab {
	public String name();

	public String dayOfWeek() default "*";

	public String month() default "*";

	public String dayOfMonth() default "*";

	public String hour() default "*";

	public String minute() default "*";

	public Class<? extends TaskListener> listener() default TaskListener.class;
}
