package scw.security.limit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 访问次数安全配置
 * 
 * @author shuchaowen
 *
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CountLimitSecurityName {
	public String value();
	
	/**
	 * 是否把全部参数做为条件
	 * @return
	 */
	public boolean condition() default false;
}