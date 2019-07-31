package scw.beans.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.beans.rpc.HttpCallDefinition;
import scw.net.http.Method;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface HttpCall {
	public String value();

	/**
	 * 要传递的header
	 * 
	 * @return
	 */
	public String[] headers() default { HttpCallDefinition.COOKIE };

	/**
	 * 以form表单形式传递
	 * 
	 * @return
	 */
	public boolean form() default true;

	public Method method() default Method.GET;
}
