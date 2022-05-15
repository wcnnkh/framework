package io.basc.framework.beans.repository;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Repository {
	Class<? extends io.basc.framework.orm.repository.Repository> value() default io.basc.framework.orm.repository.Repository.class;

	String name() default "";
}
