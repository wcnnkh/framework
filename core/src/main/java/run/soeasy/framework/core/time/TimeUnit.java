package run.soeasy.framework.core.time;

import java.util.Calendar;
import java.util.Date;

import lombok.Getter;
import lombok.NonNull;

@Getter
public abstract class TimeUnit extends TimeDiscrete {
	/**
	 * 下一个单位，可能为空
	 */
	private final TimeUnit nextTimeUnit;

	public TimeUnit(@NonNull String pattern, int calendarField, TimeUnit nextTimeUnit) {
		super(pattern, calendarField);
		this.nextTimeUnit = nextTimeUnit;
	}

	public final Date minValue(@NonNull Date value) {
		Calendar calendar = getCalendar(false);
		calendar.setTimeInMillis(value.getTime());
		setMinValue(calendar);
		return calendar.getTime();
	}

	public final Date maxValue(@NonNull Date value) {
		Calendar calendar = getCalendar(false);
		calendar.setTimeInMillis(value.getTime());
		setMaxValue(calendar);
		return calendar.getTime();
	}

	public void setMinValue(Calendar value) {
		if (nextTimeUnit == null) {
			return;
		}
		nextTimeUnit.setMinValue(value);
		value.set(nextTimeUnit.getCalendarField(), value.getActualMinimum(nextTimeUnit.getCalendarField()));
	}

	public void setMaxValue(Calendar value) {
		if (nextTimeUnit == null) {
			return;
		}
		nextTimeUnit.setMaxValue(value);
		value.set(nextTimeUnit.getCalendarField(), value.getActualMaximum(nextTimeUnit.getCalendarField()));
	}
}
