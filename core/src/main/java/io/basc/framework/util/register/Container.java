package io.basc.framework.util.register;

import io.basc.framework.util.Clearable;
import io.basc.framework.util.Elements;
import io.basc.framework.util.codec.Codec;
import io.basc.framework.util.exchange.Receipt;
import io.basc.framework.util.exchange.Registrations;
import lombok.NonNull;

/**
 * 定义一个容器
 * 
 * @author shuchaowen
 *
 * @param <E>
 */
public interface Container<E, R extends PayloadRegistration<E>> extends Registry<E>, Registrations<R>, Clearable {
	public static interface ContainerWrapper<E, R extends PayloadRegistration<E>, W extends Container<E, R>>
			extends Container<E, R>, RegistryWrapper<E, W> {

		@Override
		default boolean isEmpty() {
			return getSource().isEmpty();
		}

		@Override
		default Receipt deregisters(Elements<? extends E> elements) {
			return getSource().deregisters(elements);
		}

		@Override
		default Receipt deregister(E element) {
			return getSource().deregister(element);
		}

		@Override
		default void clear() {
			getSource().clear();
		}

		@Override
		default boolean isCancelled() {
			return getSource().isCancelled();
		}

		@Override
		default boolean isCancellable() {
			return getSource().isCancellable();
		}

		@Override
		default boolean cancel() {
			return getSource().cancel();
		}

	}

	public static class MappedContainer<S, T, R extends PayloadRegistration<S>, W extends Container<S, R>>
			extends MappedRegistry<S, T, W> implements Container<T, PayloadRegistration<T>> {

		public MappedContainer(W regisry, Codec<T, S> codec) {
			super(regisry, codec);
		}

		@Override
		public Elements<PayloadRegistration<T>> getElements() {
			return getRegistry().getElements().map((e) -> e.map(getCodec()::decode));
		}

		@Override
		public Receipt deregisters(Elements<? extends T> elements) {
			Elements<S> target = getCodec().encodeAll(elements);
			return getRegistry().deregisters(target);
		}

		@Override
		public boolean isEmpty() {
			return super.isEmpty();
		}
	}

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

	@Override
	default void clear() {
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

		clear();
		return true;
	}
}
