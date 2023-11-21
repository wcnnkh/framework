package io.basc.framework.beans.factory.config;

import java.util.concurrent.atomic.AtomicBoolean;

public class ConfigurableLifecycleProcessor extends ConfigurableServices<Lifecycle> implements LifecycleProcessor {
	private AtomicBoolean running = new AtomicBoolean();

	@Override
	public void start() {
		if (running.compareAndSet(false, true)) {
			throw new IllegalStateException("Life cycle running");
		}

		for (Lifecycle lifecycle : getServices()) {
			lifecycle.start();
		}
	}

	@Override
	public void stop() {
		if (running.compareAndSet(true, false)) {

		}

		for (Lifecycle lifecycle : getServices()) {
			lifecycle.stop();
		}
	}

	@Override
	public boolean isRunning() {
		return running.get();
	}

	@Override
	public void onRefresh() {
		start();
	}

	@Override
	public void onClose() {
		stop();
	}

}
