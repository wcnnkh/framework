package io.basc.framework.context.locks;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface LockConfig {
	public boolean all() default false;

	public long tryLockTime() default 1;

	public TimeUnit tryLockTimeUnit() default TimeUnit.SECONDS;
}
