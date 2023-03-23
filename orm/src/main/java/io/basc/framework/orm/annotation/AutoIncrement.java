package io.basc.framework.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.core.annotation.AliasFor;

/**
 * 自增字段
 * <p>
 * 一般情况下不要使用基本数据类型
 * 
 * @author wcnnkh
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@InvalidBaseTypeValue
public @interface AutoIncrement {
	@AliasFor(annotation = InvalidBaseTypeValue.class)
	double[] value() default {};
}
