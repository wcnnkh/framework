package scw.sql.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
	/**
	 * 默认的表名
	 * 
	 * @return
	 */
	public String name() default "";

	public String engine() default "InnoDB";

	public String charset() default "utf8";

	public String row_format() default "COMPACT";

	/**
	 * 是否自动创建
	 * 
	 * @return
	 */
	public boolean create() default true;

	public String comment() default "";

	/**
	 * 出现继承时字段的顺序
	 * @return
	 */
	public int sort() default 0;
}
