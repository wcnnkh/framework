package scw.beans.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.beans.auto.AutoBeanService;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoConfig {
	/**
	 * 默认的实现
	 * 
	 * @return
	 */
	public Class<?> service() default Object.class;

	/**
	 * 
	 * 尝试寻找默认实现
	 * 
	 * @return
	 */
	public Class<? extends AutoBeanService>[] autoBeanServices() default {};

	/**
	 * 尝试寻找默认实现
	 * 
	 * @return
	 */
	public String[] autoBeanServiceNames() default {};
}
