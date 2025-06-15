package run.soeasy.framework.core.time;

import java.util.Calendar;

/**
 * 周
 * 
 * @author soeasy.run
 *
 */
public class Week extends PeriodicTimeUnit {
	public static final Week DEFAULT = new Week();

	public Week() {
		super(Calendar.WEEK_OF_MONTH, 7 * Day.DEFAULT.getMillseconds());
	}

	@Override
	public void setMaxValue(Calendar value) {
		Day.DEFAULT.setMaxValue(value);
		super.setMaxValue(value);
	}

	@Override
	public void setMinValue(Calendar value) {
		Day.DEFAULT.setMinValue(value);
		super.setMinValue(value);
	}
}
