package io.basc.framework.autoconfigure.beans.factory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.core.annotation.AliasFor;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface ConfigurationProperties {
	@AliasFor("prefix")
	public String value() default "";

	@AliasFor("value")
	public String prefix() default "";
}