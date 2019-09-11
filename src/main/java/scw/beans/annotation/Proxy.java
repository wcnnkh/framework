package scw.beans.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对接口实现代理
 * @author shuchaowen
 *
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Proxy {
	/**
	 * 代理实现，请注意，此代理类必须实现filter接口
	 * @return
	 */
	public String value();
}
