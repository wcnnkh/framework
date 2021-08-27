package io.basc.framework.mvc.annotation;

import io.basc.framework.mvc.action.ActionInterceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 此值会覆盖controller中的内容,如果要追加内容请在action中添加
 * @author shuchaowen
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionInterceptors {
	/**
	 * 此值会覆盖controller中的内容,如果要追加内容请在action中添加
	 * @return
	 */
	public Class<? extends ActionInterceptor>[] value() default {};
}
