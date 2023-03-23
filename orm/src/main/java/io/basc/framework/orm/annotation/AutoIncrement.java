package io.basc.framework.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.core.annotation.AliasFor;

/**
 * 自增字段
 * <p>
 * 默认忽略值为0的数据{@link #value()}
 * 
 * @author wcnnkh
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@InvalidBaseTypeValue
public @interface AutoIncrement {
	@AliasFor(annotation = InvalidBaseTypeValue.class)
	double[] value() default { 0 };
}
