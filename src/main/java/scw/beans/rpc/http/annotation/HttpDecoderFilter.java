package scw.beans.rpc.http.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.net.DecoderFilter;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpDecoderFilter {
	public Class<? extends DecoderFilter>[] value();
	
	public String[] name() default{};
}
