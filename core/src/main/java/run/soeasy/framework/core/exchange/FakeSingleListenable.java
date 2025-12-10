package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.domain.Wrapper;
import run.soeasy.framework.core.streaming.Streamable;

@FunctionalInterface
interface FakeSingleListenable<T, W extends Listenable<Streamable<T>>> extends Listenable<T>, Wrapper<W> {
	@Override
	default Operation registerListener(Listener<T> listener) {
		BatchListener<T> batchListener = listener.batch();
		return getSource().registerListener(batchListener);
	}
}