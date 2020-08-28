package scw.freemarker.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SharedVariable {
	/**
	 * 默认使用简写类名
	 * @return
	 */
	String value() default "";
}
