package scw.beans.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.core.annotation.AliasFor;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {
	public String id() default "";
	
	@AliasFor("names")
	public String[] value() default {};
	
	/**
	 * 是否是单例
	 * 
	 * @return
	 */
	public boolean singleton() default true;
	
	public boolean proxy() default true;
}
