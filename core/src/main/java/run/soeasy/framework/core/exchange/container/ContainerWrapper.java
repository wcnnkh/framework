package run.soeasy.framework.core.exchange.container;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.Receipt;

public interface ContainerWrapper<E, R extends PayloadRegistration<E>, W extends Container<E, R>>
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
	default void reset() {
		getSource().reset();
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