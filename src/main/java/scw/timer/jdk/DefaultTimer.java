package scw.timer.jdk;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

import scw.timer.AbstractTimer;
import scw.timer.TimerTask;
import scw.timer.TimerTaskContext;
import scw.timer.TimerTaskListener;

public class DefaultTimer extends AbstractTimer {
	private final Timer timer;

	public DefaultTimer() {
		this.timer = new Timer();
	}

	public void destroy() {
		timer.cancel();
	}

	public TimerTaskContext schedule(String taskId, TimerTask task, long delay, TimeUnit timeUnit,
			TimerTaskListener timerTaskListener) {
		// TODO Auto-generated method stub
		return null;
	}

	public TimerTaskContext schedule(String taskId, TimerTask task, long delay, long period, TimeUnit timeUnit,
			TimerTaskListener timerTaskListener) {
		// TODO Auto-generated method stub
		return null;
	}

	public TimerTaskContext scheduleAtFixedRate(String taskId, TimerTask task, long delay, long period,
			TimeUnit timeUnit, TimerTaskListener timerTaskListener) {
		// TODO Auto-generated method stub
		return null;
	}
}
