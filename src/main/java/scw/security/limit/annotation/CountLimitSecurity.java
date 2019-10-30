package scw.security.limit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.security.limit.CountLimitConfigFactory;
import scw.security.limit.CountLimitFactory;

/**
 * 访问次数安全配置
 * 
 * @author shuchaowen
 *
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CountLimitSecurity {
	public Class<? extends CountLimitConfigFactory> value() default CountLimitConfigFactory.class;

	public Class<? extends CountLimitFactory> factory() default CountLimitFactory.class;
}
