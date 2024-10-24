package io.basc.framework.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.orm.ObjectRelationalResolver;

/**
 * 无效的基本数据类型值
 * 
 * @author wcnnkh
 * @see ObjectRelationalResolver#hasEffectiveValue(Object,
 *      io.basc.framework.mapper.Parameter)
 */
@Target({ ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface InvalidBaseTypeValue {
	double[] value() default {};
}
