package io.basc.framework.mvc.annotation;

import io.basc.framework.http.HttpMethod;
import io.basc.framework.mvc.action.AbstractAction;
import io.basc.framework.mvc.action.ActionInterceptor;
import io.basc.framework.mvc.action.DefaultActionManager;
import io.basc.framework.util.placeholder.PropertyResolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Controller {
	/**
	 * 行为的值，作用视请求分发器决定
	 * 在类上的注解支持使用${name:value}
	 * @see AbstractAction
	 * @see PropertyResolver
	 * @see DefaultActionManager
	 * @return
	 */
	public String value() default "";

	/**
	 * 请求方法类型
	 * 只在http中有用
	 * @return
	 */
	public HttpMethod[] methods() default {};

	public Class<? extends ActionInterceptor>[] interceptors() default {};
}
