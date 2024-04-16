package io.basc.framework.autoconfigure.orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.data.repository.IndexMethod;
import io.basc.framework.data.repository.IndexType;
import io.basc.framework.data.repository.SortOrder;

/**
 * 索引
 * 
 * @author wcnnkh
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Index {
	String name() default "";

	/**
	 * 索引类型
	 * 
	 * @see IndexType
	 * @return
	 */
	String type() default IndexType.DEFAULT_NAME;

	/**
	 * 索引长度
	 * 
	 * @return
	 */
	int length() default -1;

	/**
	 * 索引方法
	 * 
	 * @see IndexMethod
	 * @return
	 */
	String method() default IndexMethod.DEFAULT_NAME;

	/**
	 * 索引排序方式
	 * 
	 * @see SortOrder
	 * @return
	 */
	String order() default SortOrder.DEFAULT_NAME;
}
