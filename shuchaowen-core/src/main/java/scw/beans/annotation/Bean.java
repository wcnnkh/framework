package scw.beans.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {
	/**
	 * bean的id，默认是使用当前类名做为id
	 * @return
	 */
	public String value() default "";
	
	/**
	 * bean的别名
	 * @return
	 */
	public String[] names() default {};
	
	/**
	 * 是否是单例
	 * 
	 * @return
	 */
	public boolean singleton() default true;
}
