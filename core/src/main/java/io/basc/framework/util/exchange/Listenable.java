package io.basc.framework.util.exchange;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Wrapper;
import io.basc.framework.util.exchange.Listener.BatchListener;

/**
 * 可监听的
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface Listenable<T> {
	public static interface BatchListenable<T> extends Listenable<Elements<T>> {
		default Listenable<T> single() {
			return (FakeSingleListenable<T, BatchListenable<T>>) (() -> this);
		}
	}

	@FunctionalInterface
	public static interface FakeBatchListenable<T, W extends Listenable<T>> extends BatchListenable<T>, Wrapper<W> {
		@Override
		default Registration registerListener(Listener<Elements<T>> listener) {
			Listener.FakeSingleListener<T, Listener<? super Elements<T>>> singleListener = () -> listener;
			return getSource().registerListener(singleListener);
		}
	}

	@FunctionalInterface
	public static interface FakeSingleListenable<T, W extends Listenable<? extends Elements<? extends T>>>
			extends Listenable<T>, Wrapper<W> {
		@Override
		default Registration registerListener(Listener<T> listener) {
			BatchListener<T> batchListener = listener.batch();
			return getSource().registerListener(batchListener);
		}
	}

	default BatchListenable<T> batch() {
		return (FakeBatchListenable<T, Listenable<T>>) (() -> this);
	}

	/**
	 * 注册一个监听
	 * 
	 * @param listener
	 * @return
	 */
	Registration registerListener(Listener<T> listener);
}
