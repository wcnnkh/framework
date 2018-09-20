package shuchaowen.core.beans.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import shuchaowen.core.beans.config.ConfigParse;
import shuchaowen.core.beans.config.parse.PropertiesParse;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Config {
	public String value();
	public Class<? extends ConfigParse> parse() default PropertiesParse.class;
	public String charset() default "UTF-8";
}	