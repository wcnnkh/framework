package scw.beans.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 定义方法或类上的拦截器
 * @author shuchaowen
 *
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface BeanFilter {
	public Class<? extends scw.beans.BeanFilter>[] value();
	
	/**
	 * filter的名称引用
	 * @return
	 */
	public String[] names();
	
	/**
	 * 是否将名称引用的filter排在前面
	 * @return
	 */
	public boolean namePriority() default false;
}
