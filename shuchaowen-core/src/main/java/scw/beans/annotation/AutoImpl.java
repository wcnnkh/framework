package scw.beans.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.beans.loader.BeanBuilderLoader;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoImpl {
	
	/**
	 * 默认的实现
	 * 
	 * @return
	 */
	public Class<?>[] value() default {};
	
	/**
	 * 默认的实现
	 * @return
	 */
	public String[] className() default {};
	
	/**
	 * 
	 * 尝试寻找默认实现
	 * 
	 * @return
	 */
	public Class<? extends BeanBuilderLoader>[] service() default {};

	/**
	 * 尝试寻找默认实现
	 * 
	 * @return
	 */
	public String[] serviceName() default {};
}
