package io.basc.framework.timer;

import java.util.concurrent.TimeUnit;

public interface ScheduleTaskConfig extends TaskConfig {
	long getDelay();

	/**
	 * 如果是-1就说明没有周期
	 * 
	 * @return
	 */
	long getPeriod();

	TimeUnit getTimeUnit();
}
