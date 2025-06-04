package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.domain.Wrapper;

@FunctionalInterface
public interface FakeBatchListener<T, W extends Listener<? super T>> extends BatchListener<T>, Wrapper<W> {

	@Override
	default void accept(Elements<T> source) {
		source.forEach(getSource());
	}
}