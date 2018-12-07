package shuchaowen.db.annoation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
	/**
	 * 默认的表名
	 * @return
	 */
	public String name() default "";
	
	public String engine() default "InnoDB";
	
	public String charset() default "utf8";
	
	public String row_format() default "COMPACT";
	
	/**
	 * 是否自动创建
	 * @return
	 */
	public boolean create() default true;
	
	/**
	 * 是否遍历父级字段
	 * 默认是false
	 * @return
	 */
	public boolean parent() default false;
}
