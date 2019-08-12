package scw.beans.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.io.DeserializerFilter;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpDeserializerFilter {
	public Class<? extends DeserializerFilter>[] value();
	
	public String[] name() default{};
}
