package io.basc.framework.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.core.annotation.AliasFor;
import io.basc.framework.logger.Levels;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigurationProperties {
	@AliasFor("prefix")
	public String value() default "";

	@AliasFor("value")
	public String prefix() default "";

	public Levels loggerLevel() default Levels.DEBUG;
}
