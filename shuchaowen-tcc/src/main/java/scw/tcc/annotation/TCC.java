package scw.tcc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.tcc.TCCService;

/**
 * 声明一个TCC事务
 * @author shuchaowen
 *
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface TCC {
	public String confirm() default "";

	public String cancel() default "";

	/**
	 * TCC事务的服务方式， 默认是以定时器的方式重试
	 * 
	 * @return
	 */
	public Class<? extends TCCService> service() default TCCService.class;
}
