package scw.complete.method.async;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 异步执行此方法，具体效果由执行实现者决定
 * 
 * @author shuchaowen
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Async {
	/**
	 * 实现方式
	 * 
	 * @return
	 */
	public Class<? extends AsyncMethodService> service() default AsyncMethodService.class;
}
