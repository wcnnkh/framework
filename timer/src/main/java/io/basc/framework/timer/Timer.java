package io.basc.framework.timer;

import io.basc.framework.beans.annotation.AutoImpl;
import io.basc.framework.timer.support.DefaultTimer;

@AutoImpl(DefaultTimer.class)
public interface Timer {
	TaskContext getTaskContext(String taskId);

	TaskContext schedule(ScheduleTaskConfig config);

	TaskContext crontab(CrontabTaskConfig config);
}
