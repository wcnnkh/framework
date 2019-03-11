package scw.beans.rpc.transaction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.beans.rpc.transaction.service.RetryTCCService;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface TCC {
	public String confirm() default "";

	public String cancel() default "";

	public String complete() default "";

	/**
	 * TCC事务的服务方式， 默认是以定时器的方式重试
	 * 
	 * @return
	 */
	public Class<? extends TCCService> service() default RetryTCCService.class;
}
