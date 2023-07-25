package io.basc.framework.freemarker.boot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.context.annotation.Component;
import io.basc.framework.core.annotation.AliasFor;

/**
 * 标识这是一个freemarker方法
 * 
 * @author wcnnkh
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface SharedVariable {
	@AliasFor(annotation = Component.class, attribute = "value")
	String value() default "";
}
