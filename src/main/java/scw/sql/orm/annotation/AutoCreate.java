package scw.sql.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在调用save时会使用此注解
 * @author shuchaowen
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoCreate {
	/**
	 * 生成策略分组
	 * @return
	 */
	public String value() default "";
	
	/**
	 * 参数
	 * @return
	 */
	public String[] args() default {};
}
