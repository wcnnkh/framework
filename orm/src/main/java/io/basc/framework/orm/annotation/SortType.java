package io.basc.framework.orm.annotation;

import io.basc.framework.util.comparator.Sort;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 排序类型
 * 
 * @author wcnnkh
 *
 */
@Target({ ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface SortType {
	Sort value() default Sort.ASC;
}
