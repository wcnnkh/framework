package run.soeasy.framework.core.time;

import java.util.Calendar;

/**
 * 天
 * 
 * @author soeasy.run
 *
 */
public class Day extends PeriodicTimeUnit {
	public static final Day DEFAULT = new Day();

	public Day() {
		super("yyyy-MM-dd", Calendar.DAY_OF_MONTH, Hour.DEFAULT, 24L * Hour.DEFAULT.getMillseconds());
	}
}
