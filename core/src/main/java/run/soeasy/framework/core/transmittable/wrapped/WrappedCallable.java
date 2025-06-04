package run.soeasy.framework.core.transmittable.wrapped;

import java.util.concurrent.Callable;

import run.soeasy.framework.core.transmittable.Inheriter;

public class WrappedCallable<A, B, I extends Inheriter<A, B>, T, E extends Throwable, W extends Callable<? extends T>>
		extends AbstractWrapped<A, B, I, W> implements Callable<T> {

	public WrappedCallable(W source, I inheriter) {
		super(source, inheriter);
	}

	@Override
	public T call() throws Exception {
		B backup = inheriter.replay(capture);
		try {
			return this.source.call();
		} finally {
			inheriter.restore(backup);
		}
	};
}