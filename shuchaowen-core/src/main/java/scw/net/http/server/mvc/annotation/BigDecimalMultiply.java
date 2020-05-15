package scw.net.http.server.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface BigDecimalMultiply {
	/**
	 * 默认为100方便处理如价格，百分比等问题
	 * @return
	 */
	public String value() default "100";
}
