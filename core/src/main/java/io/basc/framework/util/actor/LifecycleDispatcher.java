package io.basc.framework.util.actor;

import java.util.concurrent.atomic.AtomicBoolean;

import io.basc.framework.util.Lifecycle;

public class LifecycleDispatcher extends EventDispatcher<Lifecycle> implements Lifecycle {
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
}
