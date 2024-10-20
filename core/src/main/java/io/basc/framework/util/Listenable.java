package io.basc.framework.util;

/**
 * 可监听的
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface Listenable<T> {
	/**
	 * 注册一个监听
	 * 
	 * @param listener
	 * @return
	 */
	Registration registerListener(Listener<? super T> listener);
}
