package scw.timer;

public interface CrontabConfig extends TaskConfig {
	public static final String ALL = "*";
	
	String getDayOfWeek();

	String getMonth();

	String getDayOfMonth();

	String getHour();

	String getMinute();
}
