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
		super("yyyy-MM-dd HH:mm:ss", Calendar.SECOND, 1000L);
	}

	@Override
	public void setMaxValue(Calendar value) {
		Millisecond.DEFAULT.setMaxValue(value);
		super.setMaxValue(value);
	}

	@Override
	public void setMinValue(Calendar value) {
		Millisecond.DEFAULT.setMinValue(value);
		super.setMinValue(value);
	}
}
