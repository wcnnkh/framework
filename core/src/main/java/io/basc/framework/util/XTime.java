package io.basc.framework.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class XTime {
	private XTime() {
	};

	/**
	 * 一天的毫秒数
	 */
	public static final long ONE_DAY = 86400000L;
	/**
	 * 一小时的毫秒数
	 */
	public static final long ONE_HOUR = 3600000L;
	/**
	 * 一分钟的毫秒数
	 */
	public static final long ONE_MINUTE = 60000L;
	/**
	 * 一秒的毫秒数
	 */
	public static final long ONE_SECOND = 1000L;

	/**
	 * 标准格式yyyy-MM-dd
	 */
	public static final String STANDARD_FORMAT = "yyyy-MM-dd";

	/**
	 * 获取标准时间
	 * 
	 * @param date
	 * @return
	 * @see XTime#STANDARD_FORMAT
	 */
	public static Date getStandardDate(String date) {
		return FormatUtils.parse(date, STANDARD_FORMAT);
	}

	/**
	 * 获取今日的凌晨0:00
	 * 
	 * @return
	 */
	public static long getTodayBeginTime() {
		return CalendarUtils.getDayBeginCalendar(0).getTimeInMillis();
	}

	/**
	 * 判断是否是今天
	 */
	public static boolean isToday(Date d1) {
		return isSameDay(d1, new Date());
	}

	/**
	 * 判断是否是今天
	 */
	public static boolean isToday(long time) {
		Date dt = new Date(time);
		return isSameDay(dt, new Date());
	}

	/**
	 * 判断是否是同一天
	 */
	public static boolean isSameDay(long d1, long d2) {
		SimpleDateFormat sdf = new SimpleDateFormat(STANDARD_FORMAT);
		String date1 = sdf.format(new Date(d1));
		String date2 = sdf.format(new Date(d2));
		return date1.equals(date2);
	}

	/**
	 * 判断是否是同一天
	 */
	public static boolean isSameDay(Date d1, long l2) {
		Date d2 = new Date();
		d2.setTime(l2);
		SimpleDateFormat sdf = new SimpleDateFormat(STANDARD_FORMAT);
		return sdf.format(d1).equals(sdf.format(d2));
	}

	/**
	 * 是否是同一天
	 * 
	 * @param d1
	 * @param standardTime
	 * @return
	 */
	public static boolean isSameDay(Date d1, Date standardTime) {
		SimpleDateFormat sdf = new SimpleDateFormat(STANDARD_FORMAT);
		String date1Str = sdf.format(d1);
		String date2Str = sdf.format(standardTime);
		return date1Str.equals(date2Str);
	}

	/**
	 * 将时间戳转换为指定格式的字符串
	 * @param t
	 * @param formatter
	 * @return
	 */
	public static String format(long t, String formatter) {
		Date d = new Date();
		d.setTime(t);
		return FormatUtils.format(d, formatter);
	}
	
	/**
	 * 将指定格式的字段串转换为时间戳
	 * @param time
	 * @param formatter
	 * @return
	 */
	public static long parse(String time, String formatter) {
		if(StringUtils.isEmpty(time)) {
			return 0L;
		}
		
		return FormatUtils.parse(time, formatter).getTime();
	}
}