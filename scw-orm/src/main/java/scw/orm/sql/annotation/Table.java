package scw.orm.sql.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.orm.annotation.Entity;

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

	public String row_format() default "";

	/**
	 * 是否自动创建
	 * 
	 * @return
	 */
	public boolean create() default true;

	public String comment() default "";
}
