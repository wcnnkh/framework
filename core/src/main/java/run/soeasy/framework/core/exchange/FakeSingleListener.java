package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.domain.Wrapper;
import run.soeasy.framework.core.streaming.Streamable;

@FunctionalInterface
interface FakeSingleListener<T, W extends Listener<? super Streamable<T>>> extends Listener<T>, Wrapper<W> {
	@Override
	default void accept(T source) {
		getSource().accept(Streamable.singleton(source));
	}
}