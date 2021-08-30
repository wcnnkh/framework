package io.basc.framework.timer.support;

import io.basc.framework.timer.Delayed;
import io.basc.framework.timer.ScheduleTaskConfig;
import io.basc.framework.timer.Task;
import io.basc.framework.timer.TaskListener;

import java.util.concurrent.TimeUnit;

public class SimpleTimerTaskConfig extends SimpleTaskConfig implements ScheduleTaskConfig {
	private Delayed delay;
	private long period;
	private TimeUnit timeUnit;

	public SimpleTimerTaskConfig(String taskId, Task task, TaskListener taskListener, long delay, long period,
			TimeUnit timeUnit) {
		this(taskId, task, taskListener, new SimpleDelayed(delay, timeUnit), period, timeUnit);
	}

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
