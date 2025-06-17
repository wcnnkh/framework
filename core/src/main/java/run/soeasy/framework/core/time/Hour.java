package run.soeasy.framework.core.time;

import java.util.Calendar;

/**
 * 小时
 * 
 * @author soeasy.run
 *
 */
public class Hour extends PeriodicTimeUnit {
	public static final Hour DEFAULT = new Hour();

	public Hour() {
		super("yyyy-MM-dd HH", Calendar.HOUR_OF_DAY, Minute.DEFAULT, 60 * Minute.DEFAULT.getMillseconds());
	}
}
