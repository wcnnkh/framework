package io.basc.framework.observe.mode;

import java.util.concurrent.TimeUnit;

import io.basc.framework.observe.Pull;

public abstract class PullRegistry<E extends Pull> extends ElementViewer<E> {

	@Override
	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		return true;
	}

	public boolean start() {
		return startTimerTask(this);
	}

	public boolean stop() {
		return stopTimerTask();
	}
}
