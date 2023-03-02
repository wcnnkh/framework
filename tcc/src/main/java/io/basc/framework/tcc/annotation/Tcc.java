package io.basc.framework.tcc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明一个TCC事务
 * 
 * @author wcnnkh
 *
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Tcc {
	public String beanName() default "";

	public String confirm() default "";

	public String cancel() default "";
}
