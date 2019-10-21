package scw.timer.support;

import java.util.concurrent.TimeUnit;

import scw.timer.Delayed;
import scw.timer.Task;
import scw.timer.TaskListener;
import scw.timer.TimerTaskConfig;

public class SimpleTimerTaskConfig extends SimpleTaskConfig implements TimerTaskConfig {
	private Delayed delay;
	private long period;
	private TimeUnit timeUnit;

	public SimpleTimerTaskConfig(String taskId, Task task, TaskListener taskListener, Delayed delay, long period,
			TimeUnit timeUnit) {
		super(taskId, task, taskListener);
		this.delay = delay;
		this.period = period;
		this.timeUnit = timeUnit;
	}

	public long getDelay() {
		return delay == null ? 0 : delay.getDelay(timeUnit);
	}

	public long getPeriod() {
		return period;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

}
