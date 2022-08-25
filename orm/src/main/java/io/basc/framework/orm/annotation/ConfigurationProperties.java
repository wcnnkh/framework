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
	/**
	 * 前缀(不包含后缀‘.’)
	 * 
	 * @return
	 */
	@AliasFor("prefix")
	public String value() default "";

	/**
	 * 前缀(不包含后缀‘.’)
	 * 
	 * @return
	 */
	@AliasFor("value")
	public String prefix() default "";

	/**
	 * 输出的日志等级，默认为debug
	 * 
	 * @return
	 */
	public Levels loggerLevel() default Levels.DEBUG;
}
