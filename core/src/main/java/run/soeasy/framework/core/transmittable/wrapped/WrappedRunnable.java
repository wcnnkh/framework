package run.soeasy.framework.core.transmittable.wrapped;

import run.soeasy.framework.core.transmittable.Inheriter;

public class WrappedRunnable<A, B, I extends Inheriter<A, B>, E extends Throwable, W extends Runnable>
		extends AbstractWrapped<A, B, I, W> implements Runnable {

	public WrappedRunnable(W source, I inheriter) {
		super(source, inheriter);
	}

	@Override
	public void run() {
		B backup = inheriter.replay(capture);
		try {
			this.source.run();
		} finally {
			inheriter.restore(backup);
		}
	}

}