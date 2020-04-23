package scw.async.filter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 此注解可以保证方法一定执行，但此方法一定返回空
 * 
 * @author shuchaowen
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Async {
	/**
	 * 声明调用此方法的beanName
	 * 
	 * 默认情况下使用方法所在的类调用
	 * 
	 * @return
	 */
	public String beanName() default "";

	/**
	 * 实现方式
	 * 
	 * @return
	 */
	public Class<? extends AsyncService> service() default AsyncService.class;
}
