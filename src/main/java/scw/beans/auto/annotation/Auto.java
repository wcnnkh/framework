package scw.beans.auto.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.CONSTRUCTOR })
@Retention(RetentionPolicy.RUNTIME)
public @interface Auto {
	/**
	 * 权重，不使用sort这个名字是为了方便使用
	 * @return
	 */
	public int value() default 1;
}
