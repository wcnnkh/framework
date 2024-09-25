package io.basc.framework.util.actor;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Listener;
import io.basc.framework.util.Wrapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FakeBatchListener<T, W extends Listener<? super T>>
		implements Listener<Elements<T>>, Wrapper<Listener<? super T>> {
	@NonNull
	private final W listener;

	@Override
	public void accept(Elements<T> source) {
		source.forEach(listener);
	}

	@Override
	public Listener<? super T> getSource() {
		return listener;
	}

}
