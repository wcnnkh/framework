package scw.net.http.server.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.core.annotation.KeyValuePair;

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
