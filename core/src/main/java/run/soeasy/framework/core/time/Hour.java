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
		super(Calendar.HOUR_OF_DAY, 60 * Minute.DEFAULT.getMillseconds());
	}

	@Override
	public void setMinValue(Calendar value) {
		Minute.DEFAULT.setMinValue(value);
		super.setMinValue(value);
	}

	@Override
	public void setMaxValue(Calendar value) {
		Minute.DEFAULT.setMaxValue(value);
		super.setMaxValue(value);
	}
}
