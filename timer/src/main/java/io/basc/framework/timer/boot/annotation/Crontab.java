package io.basc.framework.timer.boot.annotation;

import io.basc.framework.context.annotation.Indexed;
import io.basc.framework.timer.TaskListener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Indexed
public @interface Crontab {
	String name();

	String dayOfWeek() default "*";

	String month() default "*";

	String dayOfMonth() default "*";

	String hour() default "*";

	String minute() default "*";

	Class<? extends TaskListener> listener() default TaskListener.class;
}
