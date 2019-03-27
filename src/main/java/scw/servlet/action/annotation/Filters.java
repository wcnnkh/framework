package scw.servlet.action.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.servlet.Filter;

/**
 * 此值会覆盖controller中的内容,如果要追加内容请在action中添加
 * @author asus1
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Filters {
	/**
	 * 此值会覆盖controller中的内容,如果要追加内容请在action中添加
	 * @return
	 */
	public Class<? extends Filter>[] value() default {};
}
