package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.domain.Wrapper;

@FunctionalInterface
public interface FakeSingleListenable<T, W extends Listenable<Elements<T>>> extends Listenable<T>, Wrapper<W> {
	@Override
	default Registration registerListener(Listener<T> listener) {
		BatchListener<T> batchListener = listener.batch();
		return getSource().registerListener(batchListener);
	}
}