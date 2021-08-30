package io.basc.framework.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.annotation.KeyValuePair;

/**
 * 配置权限，如果是定义的类上的就是父级
 * @author shuchaowen
 *
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionAuthority {
	public String value();
	
	public KeyValuePair[] attributes() default {};
	
	/**
	 * 这是不是一个菜单
	 * @return
	 */
	public boolean menu() default false;
}
