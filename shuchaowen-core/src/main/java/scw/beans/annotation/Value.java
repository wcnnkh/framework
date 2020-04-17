package scw.beans.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.beans.property.ValueFormat;

/**
 * 推荐在字段添加Volatile修饰符
 * 如果字段使用final修饰则不会自动更新
 * @author shuchaowen
 *
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Value {
	public String value();
	
	public Class<? extends ValueFormat> format() default ValueFormat.class;
}
