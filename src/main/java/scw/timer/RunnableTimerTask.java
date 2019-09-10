package scw.timer;

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
