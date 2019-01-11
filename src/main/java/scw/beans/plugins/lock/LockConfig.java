package scw.beans.plugins.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface LockConfig {
	/**
	 * 指定方法参数所在的位置并使用该值参与索引的拼接
	 * @return
	 */
	public int[] keyIndex() default {};
	
	public boolean isWait() default true;
	
	public String joinChars() default "&";
}
