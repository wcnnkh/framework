package scw.timer;

public interface CrontabConfig extends TaskConfig {
	String getDayOfWeek();

	String getMonth();

	String getDayOfMonth();

	String getHour();

	String getMinute();
}
