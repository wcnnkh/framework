package scw.timer.support;

import java.util.concurrent.TimeUnit;

import scw.timer.Delayed;
import scw.timer.Task;
import scw.timer.TaskListener;
import scw.timer.ScheduleTaskConfig;

public class SimpleTimerTaskConfig extends SimpleTaskConfig implements ScheduleTaskConfig {
	private Delayed delay;
	private long period;
	private TimeUnit timeUnit;

	public SimpleTimerTaskConfig(String taskId, Task task, TaskListener taskListener, boolean distributed, long delay, long period,
			TimeUnit timeUnit) {
		this(taskId, task, taskListener, distributed, new SimpleDelayed(delay, timeUnit), period, timeUnit);
	}

	public SimpleTimerTaskConfig(String taskId, Task task, TaskListener taskListener, boolean distributed, Delayed delay, long period,
			TimeUnit timeUnit) {
		super(taskId, task, taskListener, distributed);
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
