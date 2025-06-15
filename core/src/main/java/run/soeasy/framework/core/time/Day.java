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
		super(Calendar.DAY_OF_MONTH, 24L * Hour.DEFAULT.getMillseconds());
	}

	@Override
	public void setMinValue(Calendar value) {
		Hour.DEFAULT.setMinValue(value);
		super.setMinValue(value);
	}

	@Override
	public void setMaxValue(Calendar value) {
		Hour.DEFAULT.setMaxValue(value);
		super.setMaxValue(value);
	}
}
