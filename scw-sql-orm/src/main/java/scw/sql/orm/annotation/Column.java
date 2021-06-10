package scw.sql.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.core.annotation.AliasFor;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
	@AliasFor("name")
	public String value() default "";

	@AliasFor("value")
	public String name() default "";

	/**
	 * 默认是可以为空的，但是基本数据类型(值类型)、索引、主键列是不能为空的
	 * 
	 * @return
	 */
	public boolean nullAble() default true;

	/**
	 * 是否建立唯一索引
	 * 
	 * @return
	 */
	public boolean unique() default false;

	public String comment() default "";

	public String charsetName() default "";
}
