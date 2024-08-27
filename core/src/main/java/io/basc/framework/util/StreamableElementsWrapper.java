package io.basc.framework.util;

import java.util.function.Consumer;

public interface StreamableElementsWrapper<E, W extends Streamable<E>> extends StreamableWrapper<E, W>, Elements<E> {

	@Override
	default void forEach(Consumer<? super E> action) {
		Elements.super.forEach(action);
	}

	@Override
	default SetElementsWrapper<E, ?> toSet() {
		return Elements.super.toSet();
	}

	@Override
	default ListElementsWrapper<E, ?> toList() {
		return Elements.super.toList();
	}
}
