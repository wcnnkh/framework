package io.basc.framework.sql.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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

	IndexType type() default IndexType.DEFAULT;

	int length() default -1;

	IndexMethod method() default IndexMethod.DEFAULT;

	IndexOrder order() default IndexOrder.DEFAULT;
}
