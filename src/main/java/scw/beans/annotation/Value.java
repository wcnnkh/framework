package scw.beans.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import scw.beans.property.PropertyFormat;
import scw.beans.property.ValueFormat;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Value {
	public String value();

	/**
	 * 默认的，是否刷新是由字段上是否存在volatile修饰符决定的
	 * 
	 * 如果设置为true，不管是否存在volatile修饰符都会更新,
	 * 但注意这在多线程情况下可能出现多个线程拿到的内存是不一致的，推荐使用volatile修饰符
	 * @return
	 */
	public boolean refresh() default false;
	
	/**
	 * 刷新周期
	 * 如果为0就走默认值
	 * 
	 * @return
	 */
	public long period() default 0;

	public TimeUnit timeUnit() default TimeUnit.MINUTES;

	/**
	 * format和formatName至少要存在一个，formatName优化级高
	 * @return
	 */
	public Class<? extends ValueFormat> format() default PropertyFormat.class;
	public String formatName() default "";
}
