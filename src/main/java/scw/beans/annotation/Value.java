package scw.beans.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import scw.beans.property.PropertyFactoryFormat;
import scw.beans.property.ValueFormat;

/**
 * 推荐在字段添加Volatile修饰符
 * @author shuchaowen
 *
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Value {
	public String value();
	
	/**
	 * format和formatName至少要存在一个，formatName优化级高
	 * @return
	 */
	public Class<? extends ValueFormat> format() default PropertyFactoryFormat.class;
	public String formatName() default "";

	/**
	 * 刷新周期
	 * 如果为0就走默认值
	 * 如果小于0就不刷新
	 * @return
	 */
	public long period() default 0;

	public TimeUnit timeUnit() default TimeUnit.MINUTES;
}
