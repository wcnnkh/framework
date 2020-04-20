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
	 * 是否应该实现单例
	 * 
	 * @return
	 */
	public boolean singleton() default true;

	/**
	 * 是否允许代理此类
	 * 
	 * @return
	 */
	public boolean proxy() default true;
}
