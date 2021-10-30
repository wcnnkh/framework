package io.basc.framework.sql.orm.annotation;

import io.basc.framework.orm.annotation.Entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Entity
public @interface Table {
	/**
	 * 默认的表名
	 * 
	 * @return
	 */
	public String name() default "";

	public String engine() default "";

	public String charset() default "";

	public String rowFormat() default "";

	/**
	 * 是否自动创建
	 * 
	 * @return
	 */
	public boolean create() default true;

	public String comment() default "";
}
