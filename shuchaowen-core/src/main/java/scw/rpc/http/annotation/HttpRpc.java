package scw.rpc.http.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpRpc {
	public String value();

	public ContentType requestContentType() default ContentType.AUTO;

	public static enum ContentType {
		AUTO, JSON, FORM
	}
}
