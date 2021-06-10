package scw.lang;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author shuchaowen
 *
 */
@Target({ ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD,
		ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Nullable {
	public boolean value() default true;
}
