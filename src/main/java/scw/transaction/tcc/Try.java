package scw.transaction.tcc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.transaction.tcc.service.RetryTCCService;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Try {
	/**
	 * 事务的名称
	 * @return
	 */
	public String name() default "";

	/**
	 * TCC事务的服务方式， 默认是以定时器的方式重试
	 * @return
	 */
	public Class<? extends TCCService> service() default RetryTCCService.class;
}
