package shuchaowen.web.servlet.action.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import shuchaowen.connection.http.enums.Method;

/**
 * 此值会覆盖controller中的内容,如果要追加内容请在action中添加
 * @author asus1
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Methods {
	/**
	 * 此值会覆盖controller中的内容,如果要追加内容请在action中添加
	 * @return
	 */
	public Method[] value() default {};
}
