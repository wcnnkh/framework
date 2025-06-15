package run.soeasy.framework.core.time;

import java.util.Calendar;

import lombok.Getter;

/**
 * 周期性时间单位
 * 
 * @author soeasy.run
 *
 */
@Getter
public class PeriodicTimeUnit extends TimeUnit {
	/**
	 * 毫秒数
	 */
	private final long millseconds;

	public PeriodicTimeUnit(int calendarField, long millseconds) {
		super(calendarField);
		this.millseconds = millseconds;
	}

	public long distance(Calendar start, Calendar end) {
		return Math.abs(start.getTimeInMillis() - end.getTimeInMillis()) / millseconds;
	}
}
