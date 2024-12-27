package io.basc.framework.util.exchange;

import io.basc.framework.util.Registration;

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
