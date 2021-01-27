package scw.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.context.support.DefaultContextClassesLoaderFactory;

/**
 * 使用类扫描实现Service Provider Interface
 * 
 * 应该注意在一些包下使用此注解，因为默认状态下不会扫描那些包，具体见{@link DefaultContextClassesLoaderFactory}
 * @author shuchaowen
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Provider {
	public Class<?>[] value() default {};
	
	public boolean assignableValue() default true;
	
	// 要排除的
	public Class<?>[] excludes() default {};

	public int order() default 0;
}
