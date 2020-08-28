package scw.lang;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 默认的实现是，如果在类上进行注解，说明此类所有的字段(不包含基本数据类型)默认可以(取决于value)为空
 * @author shuchaowen
 *
 */
@Target({ ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Nullable {
	public boolean value() default true;
}
