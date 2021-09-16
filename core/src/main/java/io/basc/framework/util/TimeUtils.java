package io.basc.framework.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.basc.framework.convert.ConversionException;

public class TimeUtils {
	private TimeUtils() {
	};

	/**
	 * 一天的毫秒数
	 */
	public static final int ONE_DAY = 86400000;
	/**
	 * 一小时的毫秒数
	 */
	public static final int ONE_HOUR = 3600000;
	/**
	 * 一分钟的毫秒数
	 */
	public static final int ONE_MINUTE = 60000;
	/**
	 * 一秒的毫秒数
	 */
	public static final int ONE_SECOND = 1000;

	/**
	 * 日期格式yyyy-MM-dd
	 */
	public static final String DATE_PATTERN = "yyyy-MM-dd";

	/**
	 * 时间格式 yyyy-MM-dd HH:mm:ss
	 */
	public static final String TIME_PATTERN = DATE_PATTERN + " HH:mm:ss";

	/**
	 * 时间戳格式 yyyy-MM-dd HH:mm:ss,SSS
	 */
	public static final String TIME_MILLIS_PATTERN = TIME_PATTERN + ",SSS";

	/**
	 * 将字符串转换为时间
	 * 
	 * @see DateFormat#parse(String)
	 * @param date
	 * @return
	 * @throws ConversionException
	 */
	public static Date parse(String date) throws ConversionException {
		if (StringUtils.isEmpty(date)) {
			return null;
		}

		try {
			return new SimpleDateFormat().parse(date);
		} catch (ParseException e) {
			throw new ConversionException(date, e);
		}
	}

	/**
	 * 将字符串转换为时间
	 * 
	 * @see DateFormat#parse(String)
	 * @param date
	 * @param pattern
	 * @return
	 * @throws ConversionException
	 */
	public static Date parse(String date, String pattern) throws ConversionException {
		if (StringUtils.isEmpty(date)) {
			return null;
		}

		Assert.requiredArgument(StringUtils.hasText(pattern), "pattern");
		try {
			return new SimpleDateFormat(pattern).parse(date);
		} catch (ParseException e) {
			throw new ConversionException("date=" + date + ", pattern=" + pattern, e);
		}
	}

	/**
	 * 将时间格式化为字符串
	 * 
	 * @see DateFormat#format(Date)
	 * @param date
	 * @return
	 */
	public static String format(Date date) {
		if (date == null) {
			return null;
		}

		return new SimpleDateFormat().format(date);
	}

	/**
	 * 将时间格式化为字符串
	 * 
	 * @see DateFormat#format(Date)
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String format(Date date, String pattern) {
		if (date == null) {
			return null;
		}

		Assert.requiredArgument(StringUtils.hasText(pattern), "pattern");
		return new SimpleDateFormat(pattern).format(date);
	}

	/**
	 * 获取一个月的第一天
	 * 
	 * @param millis 0表示当前时间
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
	 * @param millis 0表示当前时间
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
	 * @param millis 0表示当前时间
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
	 * @param timeInMillis 0表示当前时间
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
	 * 获取一天结束时间
	 * 
	 * @param timeInMillis 0表示当前时间
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

	/**
	 * 获取今日的凌晨0:00
	 * 
	 * @return
	 */
	public static long getTodayBeginTime() {
		return getDayBeginCalendar(0).getTimeInMillis();
	}

	/**
	 * 判断是否是今天
	 */
	public static boolean isToday(long time) {
		return isSameDay(time, System.currentTimeMillis());
	}

	/**
	 * 判断是否是同一天
	 */
	public static boolean isSameDay(long d1, long d2) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
		String date1 = sdf.format(new Date(d1));
		String date2 = sdf.format(new Date(d2));
		return date1.equals(date2);
	}

	/**
	 * 将时间戳转换为指定格式的字符串
	 * 
	 * @param time
	 * @param pattern
	 * @return
	 */
	public static String format(long time, String pattern) {
		Date d = new Date();
		d.setTime(time);
		return format(d, pattern);
	}

	/**
	 * 将指定格式的字段串转换为时间戳
	 * 
	 * @param time
	 * @param pattern
	 * @return
	 */
	public static long getTime(String time, String pattern) throws ConversionException {
		Date date = parse(time, pattern);
		return date == null ? 0L : date.getTime();
	}
}
