package scw.core.instance.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用类扫描实现Service Provider Interface
 * @author shuchaowen
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SPI {
	public Class<?>[] value() default {};
	
	public boolean assignableValue() default true;
	
	// 要排除的
	public Class<?>[] excludes() default {};

	public int order() default 0;
}
