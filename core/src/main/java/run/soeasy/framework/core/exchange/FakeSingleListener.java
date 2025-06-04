package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.domain.Wrapper;

@FunctionalInterface
public interface FakeSingleListener<T, W extends Listener<? super Elements<T>>> extends Listener<T>, Wrapper<W> {
	@Override
	default void accept(T source) {
		getSource().accept(Elements.singleton(source));
	}
}