package io.basc.framework.autoconfigure.transaction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.transaction.Isolation;
import io.basc.framework.transaction.Propagation;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Transactional {
	public Propagation propagation() default Propagation.REQUIRED;

	public boolean readOnly() default false;

	public Isolation isolation() default Isolation.DEFAULT;

	public int timeout() default -1;

	public Class<? extends Throwable> rollbackFor() default Throwable.class;
}