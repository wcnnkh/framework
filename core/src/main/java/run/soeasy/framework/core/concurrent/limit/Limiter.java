package run.soeasy.framework.core.concurrent.limit;

import java.util.concurrent.locks.Lock;

/**
 * 限制器
 * 
 * @author shuchaowen
 *
 */
public interface Limiter {
	/**
	 * 是否受限的
	 * 
	 * @return
	 */
	boolean isLimited();

	/**
	 * 标记为受限的。调用此方法后{@link #isLimited()}将始终返回true
	 */
	boolean limited();

	/**
	 * 获取资源
	 * 
	 * @return
	 */
	Lock getResource();
}
