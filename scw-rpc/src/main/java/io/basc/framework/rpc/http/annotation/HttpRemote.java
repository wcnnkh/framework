package io.basc.framework.rpc.http.annotation;

import io.basc.framework.core.Constants;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.http.MediaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpRemote {
	String value();
	
	HttpMethod method() default HttpMethod.GET;
	
	String contentType() default MediaType.APPLICATION_FORM_URLENCODED_VALUE;
	
	String charsetName() default Constants.UTF_8_NAME;
}
