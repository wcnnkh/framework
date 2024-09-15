package io.basc.framework.util;

/**
 * 可观测的
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface Observable<T> {
	/**
	 * 注册一个观察者
	 * 
	 * @param listener
	 * @return
	 */
	Registration registerListener(Listener<? super T> listener);
}
