package io.basc.framework.util.register;

import java.util.function.BooleanSupplier;

import io.basc.framework.util.Lifecycle;
import io.basc.framework.util.concurrent.limit.DisposableLimiter;
import io.basc.framework.util.exchange.Listenable;
import io.basc.framework.util.exchange.Listener;
import io.basc.framework.util.exchange.Registration;
import io.basc.framework.util.exchange.event.LifecycleDispatcher;

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
