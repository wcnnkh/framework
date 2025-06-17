package run.soeasy.framework.core.time;

import java.util.Calendar;
import java.util.Date;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.domain.Discrete;

@Getter
public abstract class TimeUnit extends TimeFormat implements Discrete<Date> {
	/**
	 * 日历中的字段
	 */
	private final int calendarField;

	public TimeUnit(@NonNull String pattern, int calendarField) {
		super(pattern);
		this.calendarField = calendarField;
	}

	/**
	 * 获取Calendar
	 * 
	 * @param forceCreate 是否强制创建一个新的
	 * @return
	 */
	public Calendar getCalendar(boolean forceCreate) {
		return Calendar.getInstance();
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

	@Override
	public long distance(Date start, Date end) {
		Calendar startCalendar = getCalendar(false);
		Calendar endCalendar = getCalendar(true);
		return distance(startCalendar, endCalendar);
	}

	@Override
	public Date next(Date value) {
		Calendar calendar = getCalendar(false);
		calendar.setTimeInMillis(value.getTime());
		next(calendar, 1);
		return calendar.getTime();
	}

	@Override
	public Date previous(Date value) {
		Calendar calendar = getCalendar(false);
		calendar.setTimeInMillis(value.getTime());
		previous(calendar, 1);
		return calendar.getTime();
	}

	public void setMinValue(Calendar value) {
		value.set(getCalendarField(), value.getActualMinimum(getCalendarField()));
	}

	public void setMaxValue(Calendar value) {
		value.set(getCalendarField(), value.getActualMaximum(getCalendarField()));
	}

	public void next(Calendar value, int unit) {
		value.add(calendarField, value.get(calendarField) + unit);
	}

	public void previous(Calendar value, int unit) {
		value.add(calendarField, value.get(calendarField) - unit);
	}

	public abstract long distance(Calendar start, Calendar end);
}
