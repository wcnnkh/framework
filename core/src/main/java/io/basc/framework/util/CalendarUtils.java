package io.basc.framework.util;

import java.util.Calendar;

public final class CalendarUtils {
	private CalendarUtils() {
	};

	/**
	 * 获取一个月的第一天
	 * 
	 * @param millis
	 * @return
	 */
	public static Calendar getMonthBeginCalendar(long millis) {
		Calendar calendar = Calendar.getInstance();
		if (millis > 0) {
			calendar.setTimeInMillis(millis);
		}
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));
		return calendar;
	}

	/**
	 * 获取一个月的最后一天
	 * 
	 * @param millis
	 * @return
	 */
	public static Calendar getMonthEndCalendar(long millis) {
		Calendar calendar = Calendar.getInstance();
		if (millis > 0) {
			calendar.setTimeInMillis(millis);
		}
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));
		return calendar;
	}

	/**
	 * 获取一年的第一天
	 * 
	 * @param millis
	 * @return
	 */
	public static Calendar getYearBeginCalendar(long millis) {
		Calendar calendar = Calendar.getInstance();
		if (millis > 0) {
			calendar.setTimeInMillis(millis);
		}
		calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMinimum(Calendar.DAY_OF_YEAR));
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));
		return calendar;
	}

	/**
	 * 获取一年的最后一天
	 * 
	 * @param millis
	 * @return
	 */
	public static Calendar getYearEndCalendar(long millis) {
		Calendar calendar = Calendar.getInstance();
		if (millis > 0) {
			calendar.setTimeInMillis(millis);
		}
		calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));
		return calendar;
	}

	/**
	 * 获取一天凌晨时间
	 * 
	 * @param timeInMillis
	 * @return
	 */
	public static Calendar getDayBeginCalendar(long millis) {
		Calendar calendar = Calendar.getInstance();
		if (millis > 0) {
			calendar.setTimeInMillis(millis);
		}
		calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));
		return calendar;
	}

	/**
	 * 获取一天凌晨时间
	 * 
	 * @param timeInMillis
	 * @return
	 */
	public static Calendar getDayEndCalendar(long millis) {
		Calendar calendar = Calendar.getInstance();
		if (millis > 0) {
			calendar.setTimeInMillis(millis);
		}

		calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));
		return calendar;
	}
}
