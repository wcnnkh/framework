package io.basc.framework.timer.support;

import java.util.TimerTask;

public final class RunnableTimerTask extends TimerTask {
	private final Runnable runnable;

	public RunnableTimerTask(Runnable runnable) {
		this.runnable = runnable;
	}

	@Override
	public void run() {
		runnable.run();
	}
}
