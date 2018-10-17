package shuchaowen.core.beans.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import shuchaowen.core.beans.BeanFilter;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Bean {
	public String id() default "";
	public String[] names() default {};
	public boolean singleton() default true;
	public Class<? extends BeanFilter>[] beanFilters() default {};
	public String factoryMethod() default "";
}
