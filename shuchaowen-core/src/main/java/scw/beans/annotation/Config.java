package scw.beans.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.beans.config.ConfigParse;
import scw.beans.config.parse.PropertiesParse;

/**
 * 推荐使用@Value注解
 * @author shuchaowen
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Config {
	public String value();
	public Class<? extends ConfigParse> parse() default PropertiesParse.class;
	public String charset() default "UTF-8";
}	