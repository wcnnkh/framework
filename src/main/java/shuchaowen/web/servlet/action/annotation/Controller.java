package shuchaowen.web.servlet.action.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import shuchaowen.common.enums.HttpMethod;
import shuchaowen.web.servlet.action.Filter;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Controller {
	/**
	 * 行为的值，作用视请求分发器决定
	 * @return
	 */
	public String value() default "";
	
	/**
	 * 请求方法类型
	 * @return
	 */
	public HttpMethod[] httpMethods() default {};
	
	/**
	 * filters
	 * @return
	 */
	public Class<? extends Filter>[] filters() default {};
}
