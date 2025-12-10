package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.domain.Wrapper;
import run.soeasy.framework.core.streaming.Streamable;

@FunctionalInterface
interface FakeBatchListener<T, W extends Listener<? super T>> extends BatchListener<T>, Wrapper<W> {

	@Override
	default void accept(Streamable<T> source) {
		source.forEach(getSource());
	}
}