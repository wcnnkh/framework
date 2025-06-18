package run.soeasy.framework.core.time;

import java.util.Calendar;

/**
 * 秒
 * 
 * @author soeasy.run
 *
 */
public class Second extends PeriodicTimeUnit {
	public static final Second DEFAULT = new Second();

	public Second() {
		super("yyyy-MM-dd HH:mm:ss", Calendar.SECOND, Millisecond.DEFAULT, 1000L);
	}
}
