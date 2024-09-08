package io.basc.framework.util.observe.event;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Wrapper;
import io.basc.framework.util.observe.Listener;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FakeSingleListener<T, W extends Listener<? super Elements<T>>> implements Listener<T>, Wrapper<W> {
	@NonNull
	private final W listener;

	@Override
	public void accept(T source) {
		listener.accept(Elements.singleton(source));
	}

	@Override
	public W getSource() {
		return listener;
	}
}
