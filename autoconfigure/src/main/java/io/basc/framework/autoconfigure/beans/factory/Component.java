package io.basc.framework.autoconfigure.beans.factory;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.autoconfigure.stereotype.Indexed;

/**
 * 包含到容器中
 * 
 * @author wcnnkh
 *
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
@Singleton
public @interface Component {
	String value() default "";
}