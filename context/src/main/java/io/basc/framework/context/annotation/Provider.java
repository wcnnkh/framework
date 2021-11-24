package io.basc.framework.context.annotation;

import io.basc.framework.core.Ordered;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用类扫描实现Service Provider Interface
 * 
 * @author shuchaowen
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Indexed
public @interface Provider {
	public Class<?>[] value() default {};

	public boolean assignableValue() default true;

	// 要排除的
	public Class<?>[] excludes() default {};

	/**
	 * 从小到大
	 * 
	 * @see Ordered#DEFAULT_PRECEDENCE
	 * @return
	 */
	public int order() default Ordered.DEFAULT_PRECEDENCE;
}
