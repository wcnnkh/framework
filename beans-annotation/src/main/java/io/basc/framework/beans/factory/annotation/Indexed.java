package io.basc.framework.beans.factory.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.core.annotation.AliasFor;
import io.basc.framework.core.annotation.Order;

/**
 * 上下文扫描时会允许加入上下文
 * 
 * @author wcnnkh
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Order
public @interface Indexed {
	@AliasFor(annotation = Order.class)
	int value() default 0;
}