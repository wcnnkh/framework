package scw.beans.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 是否开启事务内的查询缓存
 * 注解后默认不开启，因为默认是开启查询缓存的
 * @author shuchaowen
 *
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SelectCache {
	public boolean value() default false;
}