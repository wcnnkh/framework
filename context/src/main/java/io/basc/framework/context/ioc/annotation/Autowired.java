package io.basc.framework.context.ioc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Autowired {
	public String value() default "";

	/**
	 * 是否强制依赖, 如果为false，那么仅当field不存在或字段的值为空时才注入
	 * 
	 * @see AutowiredIocProcessor
	 * @return
	 */
	public boolean required() default true;
}
