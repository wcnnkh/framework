package scw.timer;

import java.util.concurrent.TimeUnit;

import scw.core.Destroy;

public interface Timer extends Destroy {
	TimerTaskContext schedule(String taskId, TimerTask task, long delay, TimeUnit timeUnit,
			TimerTaskListener timerTaskListener);

	TimerTaskContext schedule(String taskId, TimerTask task, long delay, long period, TimeUnit timeUnit,
			TimerTaskListener timerTaskListener);

	TimerTaskContext scheduleAtFixedRate(String taskId, TimerTask task, long delay, long period, TimeUnit timeUnit,
			TimerTaskListener timerTaskListener);
}
