package io.basc.framework.orm.stereotype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.data.repository.SortOrder;

/**
 * 排序类型
 * 
 * @author wcnnkh
 *
 */
@Target({ ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface SortType {
	String value() default SortOrder.DEFAULT_NAME;
}
