package scw.beans.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.beans.support.DefaultBeanDefinition;
import scw.core.annotation.AliasFor;
import scw.logger.Levels;

/**
 * 该行为发生在autowired之后aware之前<br/>
 * 当存在scw.beans.*的注解字段不会进行属性注入<br/>
 * 注意'前缀'不应该包含'.'之类的后缀<br/>
 * @see DefaultBeanDefinition#dependence(Object)
 * @see IgnoreConfigurationProperty
 * @author shuchaowen
 *
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigurationProperties {
	/**
	 * 前缀(不包含后缀‘.’)
	 * @return
	 */
	@AliasFor("prefix")
	public String value() default "";
	/**
	 * 前缀(不包含后缀‘.’)
	 * @return
	 */
	@AliasFor("value")
	public String prefix() default "";
	
	/**
	 * 输出的日志等级，默认为debug
	 * @return
	 */
	public Levels loggerLevel() default Levels.DEBUG;
}
