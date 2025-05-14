package run.soeasy.framework.core.exchange.container;

import java.util.function.BooleanSupplier;

import run.soeasy.framework.core.concurrent.limit.DisposableLimiter;
import run.soeasy.framework.core.exchange.Lifecycle;
import run.soeasy.framework.core.exchange.Listenable;
import run.soeasy.framework.core.exchange.Listener;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.event.LifecycleDispatcher;

public abstract class AbstractLifecycleRegistration extends LimitableRegistration
		implements LifecycleRegistration, Listenable<Lifecycle> {
	private final LifecycleDispatcher dispatcher = new LifecycleDispatcher();

	public AbstractLifecycleRegistration() {
		super(new DisposableLimiter());
	}

	@Override
	public Registration registerListener(Listener<Lifecycle> listener) {
		return dispatcher.registerListener(listener);
	}

	@Override
	public boolean cancel(BooleanSupplier cancel) {
		try {
			return super.cancel(cancel);
		} finally {
			stop();
		}
	}

	@Override
	public void start() {
		if (isCancelled()) {
			throw new IllegalStateException("Registration Cancelled");
		}

		dispatcher.start();
	}

	@Override
	public void stop() {
		dispatcher.stop();
	}

	@Override
	public boolean isRunning() {
		return dispatcher.isRunning();
	}
}
