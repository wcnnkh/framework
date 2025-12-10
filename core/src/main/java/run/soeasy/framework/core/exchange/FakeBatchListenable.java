package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.domain.Wrapper;
import run.soeasy.framework.core.streaming.Streamable;

@FunctionalInterface
interface FakeBatchListenable<T, W extends Listenable<T>> extends BatchListenable<T>, Wrapper<W> {
	@Override
	default Operation registerListener(Listener<Streamable<T>> listener) {
		FakeSingleListener<T, Listener<Streamable<T>>> singleListener = () -> listener;
		return getSource().registerListener(singleListener);
	}
}