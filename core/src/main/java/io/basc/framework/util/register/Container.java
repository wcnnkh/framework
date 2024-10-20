package io.basc.framework.util.register;

import java.util.Arrays;

import io.basc.framework.util.Clearable;
import io.basc.framework.util.Receipt;
import io.basc.framework.util.Registrations;

/**
 * 定义一个容器
 * 
 * @author shuchaowen
 *
 * @param <E>
 */
public interface Container<E, R extends PayloadRegistration<E>> extends Registry<E>, Registrations<R>, Clearable {
	/**
	 * 取消登记
	 * 
	 * @param element
	 * @return
	 */
	default Receipt deregister(E element) {
		return deregisters(Arrays.asList(element));
	}

	/**
	 * 只要有一个成功就是成功
	 * 
	 * @param elements
	 * @return
	 */
	Receipt deregisters(Iterable<? extends E> elements);

	@Override
	default boolean isEmpty() {
		return Registry.super.isEmpty();
	}

	@Override
	default void clear() {
		deregisters(this);
	}

	@Override
	default boolean isCancelled() {
		return isEmpty();
	}

	@Override
	default boolean isCancellable() {
		return !isEmpty();
	}

	@Override
	default boolean cancel() {
		if (isEmpty()) {
			return false;
		}

		clear();
		return true;
	}
}
