package io.basc.framework.util.exchange.event;

import java.util.concurrent.atomic.AtomicBoolean;

import io.basc.framework.util.Lifecycle;
import io.basc.framework.util.LifecycleProcessor;

public class LifecycleDispatcher extends EventDispatcher<Lifecycle> implements LifecycleProcessor {
	private AtomicBoolean started = new AtomicBoolean();

	@Override
	public void start() {
		if (started.compareAndSet(false, true)) {
			this.publish(this);
		}
	}

	@Override
	public void stop() {
		if (started.compareAndSet(true, false)) {
			this.publish(this);
		}
	}

	@Override
	public boolean isRunning() {
		return started.get();
	}

	@Override
	public void onRefresh() {
		try {
			this.publish(this);
		} finally {
			started.set(true);
		}
	}

	@Override
	public void onClose() {
		started.set(false);
		this.publish(this);
	}
}
