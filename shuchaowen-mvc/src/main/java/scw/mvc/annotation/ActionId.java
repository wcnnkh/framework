package scw.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionId {
	/**
	 * 如果注解在类上就那么actionId就以此值开头, 如果注解在方法上那 么actionId就等于此值
	 * @author shuchaowen
	 */
	public String value();
}
