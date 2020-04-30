package scw.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.core.annotation.KeyValuePair;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Authority {
	public String value();
	
	public KeyValuePair[] attributes() default {};
	
	/**
	 * 设置为false就说明这不是一个menu,默认为true(系统自动判定)
	 * @return
	 */
	public boolean menuAction() default true;
}
