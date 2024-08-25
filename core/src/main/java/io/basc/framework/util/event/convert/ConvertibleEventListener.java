package io.basc.framework.util.event.convert;

import java.util.function.Function;

import io.basc.framework.util.event.EventListener;
import lombok.Data;
import lombok.NonNull;

@Data
public class ConvertibleEventListener<S, E> implements EventListener<E> {
	@NonNull
	private final EventListener<S> eventListener;
	@NonNull
	private final Function<? super E, ? extends S> converter;

	@Override
	public void onEvent(E event) {
		S source = converter.apply(event);
		eventListener.onEvent(source);
	}
}
