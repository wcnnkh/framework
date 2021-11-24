package io.basc.framework.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.util.StringMatchers;
import io.basc.framework.value.PropertyFactory;

/**
 * 有条件的启用
 * ${condition} = value
 * @author shuchaowen
 * @see EnableConditionUtils
 * @see PropertyFactory#getString(String)
 * @see StringMatchers#SIMPLE
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Indexed
public @interface EnableCondition {
	
	/**
	 * 满足一个即可
	 * @return
	 */
	String[] value() default {"true"};
	
	/**
	 *  条件
	 * @return
	 */
	String condition();
}
