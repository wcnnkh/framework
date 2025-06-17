package run.soeasy.framework.core.time;

import java.util.Calendar;

/**
 * 分钟
 * 
 * @author soeasy.run
 *
 */
public class Minute extends PeriodicTimeUnit {
	public static final Minute DEFAULT = new Minute();

	public Minute() {
		super("yyyy-MM-dd HH:mm", Calendar.MINUTE, Second.DEFAULT, 60 * Second.DEFAULT.getMillseconds());
	}
}
