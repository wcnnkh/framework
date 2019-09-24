package scw.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.mvc.Filter;
import scw.mvc.ParameterFilter;
import scw.net.http.Method;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Controller {
	/**
	 * 行为的值，作用视请求分发器决定
	 * 
	 * @return
	 */
	public String value() default "";

	/**
	 * 请求方法类型
	 * 只在http中有用
	 * @return
	 */
	public Method[] methods() default {};

	/**
	 * filters
	 * 
	 * @return
	 */
	public Class<? extends Filter>[] filters() default {};

	/**
	 * 参数解析   不处理基本数据类型及基本数据类型的包装类型、枚举、Class
	 * @return
	 */
	public Class<? extends ParameterFilter>[] parameterFilter() default {};
	
	public String name() default "";
}
