package scw.beans.plugins.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Cache {
	/**
	 * 字符串标识的前缀
	 * @return
	 */
	public String prefix() default "";
	
	/**
	 * 指定方法参数所在的位置并使用该值参与索引的拼接 默认是把所有的参数组装起来
	 * 
	 * @return
	 */
	public int[] keyIndex() default {};
	
	/**
	 * 缓存失效时间 默认10分钟
	 * 
	 * @return
	 */
	public int exp() default 10;
	
	public TimeUnit timeUnit() default TimeUnit.MINUTES;
}