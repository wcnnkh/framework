package run.soeasy.framework.core.time;

import java.util.Calendar;

import lombok.Getter;
import lombok.NonNull;

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

	public PeriodicTimeUnit(@NonNull String pattern, int calendarField, TimeUnit nextTimeUnit, long millseconds) {
		super(pattern, calendarField, nextTimeUnit);
		this.millseconds = millseconds;
	}

	public long distance(Calendar start, Calendar end) {
		return Math.abs(start.getTimeInMillis() - end.getTimeInMillis()) / millseconds;
	}
}
