package scw.beans.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.beans.rpc.HttpProxyDefinition;
import scw.net.http.Method;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpProxy {
	public String value();

	/**
	 * 要传递的header
	 * 
	 * @return
	 */
	public String[] headers() default { HttpProxyDefinition.COOKIE };

	/**
	 * 以form表单形式传递
	 * 
	 * @return
	 */
	public boolean form() default true;

	public Method method() default Method.GET;
}
