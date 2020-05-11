package scw.security.authority.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.core.annotation.KeyValuePair;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthorityConfig {
	/**
	 * 显示声明id, 默认由实现者生成id
	 * @return
	 */
	public String id() default "";
	
	public String value();
	
	public KeyValuePair[] attributes() default {};
	
	/**
	 * 这是不是一个菜单
	 * @return
	 */
	public boolean menu() default false;
	
	/**
	 * 子权限
	 * @return
	 */
	public Class<?>[] children() default {};
}
