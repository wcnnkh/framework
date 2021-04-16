package scw.beans.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.beans.support.DefaultBeanDefinition;
import scw.core.annotation.AliasFor;
import scw.logger.Level;

/**
 * 该行为发生在aware之前
 * @see DefaultBeanDefinition#dependence(Object)
 * @author shuchaowen
 *
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigurationProperties {
	@AliasFor("prefix")
	public String value() default "";
	@AliasFor("value")
	public String prefix() default "";
	
	/**
	 * 输出的日志等级，默认为debug
	 * @return
	 */
	public Level loggerLevel() default Level.DEBUG;
}
