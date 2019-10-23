package scw.timer;

import scw.beans.annotation.AutoImpl;
import scw.timer.support.DefaultTimer;

@AutoImpl(DefaultTimer.class)
public interface Timer {
	TaskContext getTaskContext(String taskId);

	TaskContext schedule(ScheduleTaskConfig config);

	TaskContext crontab(CrontabConfig config);
}
