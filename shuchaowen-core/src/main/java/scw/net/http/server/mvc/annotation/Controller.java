package scw.net.http.server.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.net.http.HttpMethod;
import scw.net.http.server.mvc.action.ActionFilter;

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
	public HttpMethod[] methods() default {};

	/**
	 * filters
	 * 
	 * @return
	 */
	public Class<? extends ActionFilter>[] filters() default {};
}
