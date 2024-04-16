package io.basc.framework.autoconfigure.beans.factory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.autoconfigure.stereotype.Indexed;

/**
 * 有条件的启用 ${condition} = value
 * 
 * @author wcnnkh
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Indexed
public @interface EnableCondition {

	String[] value() default { "true" };

	String condition();
}
