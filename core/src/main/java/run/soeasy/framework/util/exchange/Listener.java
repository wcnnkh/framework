package run.soeasy.framework.util.exchange;

import java.util.EventListener;
import java.util.function.Consumer;

import run.soeasy.framework.util.collection.Elements;
import run.soeasy.framework.util.function.Wrapper;

public interface Listener<T> extends Consumer<T>, EventListener {
	@Override
	void accept(T source);

	default BatchListener<T> batch() {
		return (FakeBatchListener<T, Listener<T>>) (() -> this);
	}

	@FunctionalInterface
	public static interface FakeBatchListener<T, W extends Listener<? super T>> extends BatchListener<T>, Wrapper<W> {

		@Override
		default void accept(Elements<T> source) {
			source.forEach(getSource());
		}
	}

	@FunctionalInterface
	public static interface FakeSingleListener<T, W extends Listener<? super Elements<T>>>
			extends Listener<T>, Wrapper<W> {
		@Override
		default void accept(T source) {
			getSource().accept(Elements.singleton(source));
		}
	}

	public static interface BatchListener<T> extends Listener<Elements<T>> {

		default Listener<T> single() {
			return (FakeSingleListener<T, BatchListener<T>>) (() -> this);
		}
	}
}
