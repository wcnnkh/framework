package io.basc.framework.timer;

public interface Timer {
	TaskContext getTaskContext(String taskId);

	TaskContext schedule(ScheduleTaskConfig config);

	TaskContext crontab(CrontabTaskConfig config);
}