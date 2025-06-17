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
		super("yyyy-MM-dd HH:mm", Calendar.MINUTE, 60 * Second.DEFAULT.getMillseconds());
	}

	@Override
	public void setMinValue(Calendar value) {
		Second.DEFAULT.setMinValue(value);
		super.setMinValue(value);
	}

	@Override
	public void setMaxValue(Calendar value) {
		Second.DEFAULT.setMaxValue(value);
		super.setMaxValue(value);
	}
}
