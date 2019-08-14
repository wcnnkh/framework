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

	public boolean refresh() default true;

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
