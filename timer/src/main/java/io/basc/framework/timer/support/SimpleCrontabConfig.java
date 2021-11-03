package io.basc.framework.timer.support;

import io.basc.framework.timer.CrontabTaskConfig;
import io.basc.framework.timer.Task;
import io.basc.framework.timer.TaskListener;
import io.basc.framework.timer.boot.annotation.Crontab;
import io.basc.framework.util.StringUtils;

public class SimpleCrontabConfig extends SimpleTaskConfig implements CrontabTaskConfig {
	private String dayOfWeek;
	private String month;
	private String dayOfMonth;
	private String hour;
	private String minute;

	public SimpleCrontabConfig(Crontab crontab, Task task, TaskListener taskListener) {
		super(crontab.name(), task, taskListener);
		this.dayOfWeek = crontab.dayOfWeek();
		this.month = crontab.month();
		this.dayOfMonth = crontab.dayOfMonth();
		this.hour = crontab.hour();
		this.minute = crontab.minute();
	}

	public SimpleCrontabConfig(String taskId, Task task, TaskListener taskListener, String dayOfWeek, String month,
			String dayOfMonth, String hour, String minute) {
		super(taskId, task, taskListener);
		this.dayOfWeek = dayOfWeek;
		this.month = month;
		this.dayOfMonth = dayOfMonth;
		this.hour = hour;
		this.minute = minute;
	}

	public String getDayOfWeek() {
		return StringUtils.isEmpty(dayOfWeek) ? ALL : dayOfWeek;
	}

	public String getMonth() {
		return StringUtils.isEmpty(month) ? ALL : month;
	}

	public String getDayOfMonth() {
		return StringUtils.isEmpty(dayOfMonth) ? ALL : dayOfMonth;
	}

	public String getHour() {
		return StringUtils.isEmpty(hour) ? ALL : hour;
	}

	public String getMinute() {
		return StringUtils.isEmpty(minute) ? ALL : minute;
	}

}
