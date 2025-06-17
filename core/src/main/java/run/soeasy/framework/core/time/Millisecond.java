package run.soeasy.framework.core.time;

import java.util.Calendar;

/**
 * 毫秒
 * 
 * @author soeasy.run
 *
 */
public class Millisecond extends PeriodicTimeUnit {
	public static final Millisecond DEFAULT = new Millisecond();

	public Millisecond() {
		super("yyyy-MM-dd HH:mm:ss,SSS", Calendar.MILLISECOND, 1L);
	}
}
