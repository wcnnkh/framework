package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.domain.Wrapper;

@FunctionalInterface
public interface FakeBatchListenable<T, W extends Listenable<T>> extends BatchListenable<T>, Wrapper<W> {
	@Override
	default Registration registerListener(Listener<Elements<T>> listener) {
		FakeSingleListener<T, Listener<Elements<T>>> singleListener = () -> listener;
		return getSource().registerListener(singleListener);
	}
}