package run.soeasy.framework.core.exchange.container;

import lombok.NonNull;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.Receipt;
import run.soeasy.framework.core.exchange.Registrations;

/**
 * 定义一个容器
 * 
 * @author shuchaowen
 *
 * @param <E>
 */
public interface Container<E, R extends PayloadRegistration<E>> extends Registry<E>, Registrations<R> {
	/**
	 * 取消登记
	 * 
	 * @param element
	 * @return
	 */
	default Receipt deregister(E element) {
		return deregisters(Elements.singleton(element));
	}

	/**
	 * 只要有一个成功就是成功
	 * 
	 * @param elements
	 * @return
	 */
	Receipt deregisters(Elements<? extends E> elements);

	@Override
	default boolean isEmpty() {
		return Registry.super.isEmpty();
	}

	default void reset() {
		deregisters(this);
	}

	@Override
	default <T> Registry<T> map(@NonNull Codec<T, E> codec) {
		return new MappedContainer<>(this, codec);
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

		reset();
		return true;
	}
}
