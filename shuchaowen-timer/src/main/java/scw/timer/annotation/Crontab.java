package scw.timer.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.timer.TaskListener;

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
