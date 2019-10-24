package scw.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.security.ip.IPVerification;

/**
 * 使用指定的实现对ip进行校验
 * @author shuchaowen
 *
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface IPSecurity {
	public Class<? extends IPVerification> value() default IPVerification.class;
}
