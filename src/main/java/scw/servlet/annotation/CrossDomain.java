package scw.servlet.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CrossDomain {
	/**
	 * 是否启用，默认启用
	 * @return
	 */
	public boolean enable() default true;
	
	/**
	 * 允许跨域请求的域
	 * @return
	 */
	public String origin() default "*";

	/**
	 * 重新预检验跨域的缓存时间 (s) 
	 * @return
	 */
	public int maxAge() default 3600;
	
	/**
	 * 允许跨域的请求头
	 * @return
	 */
	public String headers() default "*";

	/**
	 * 是否携带cookie
	 * @return
	 */
	public boolean credentials() default true;
}
