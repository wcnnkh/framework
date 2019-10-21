package scw.timer;

import scw.beans.annotation.AutoImpl;
import scw.timer.support.DefaultTimer;

@AutoImpl(DefaultTimer.class)
public interface Timer {
	void schedule(TimerTaskConfig config);

	void scheduleAtFixedRate(TimerTaskConfig config);

	void crontab(CrontabConfig config);
}
