package scw.beans.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {
	/**
	 * 是否应该实现单例
	 * @return
	 */
	public boolean singleton() default true;
	
	/**
	 * 是否允许代理此类
	 * @return
	 */
	public boolean proxy() default true;
}
