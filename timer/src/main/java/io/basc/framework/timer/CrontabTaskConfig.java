package io.basc.framework.timer;

public interface CrontabTaskConfig extends TaskConfig {
	public static final String ALL = "*";
	
	String getDayOfWeek();

	String getMonth();

	String getDayOfMonth();

	String getHour();

	String getMinute();
}
