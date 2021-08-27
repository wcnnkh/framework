package io.basc.framework.core.utils;

import io.basc.framework.lang.FormatterException;
import io.basc.framework.util.CalendarUtils;
import io.basc.framework.util.FormatUtils;

import java.text.ParseException;
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

	public static final String yyyy_MM_dd = "yyyy-MM-dd";

	/**
	 * 将时间格式和时间字符串值传入，获得时间戳<br>
	 * 举例时间格式 yyyy-MM-dd hh:mm:ss
	 * 
	 * @param timeStr
	 *            ,时间2010-09-12 00:00:00
	 * @param format
	 *            ,时间格式 yyyy-MM-dd hh:mm:ss
	 * @return
	 */
	public static long getTime(String timeStr, String format) throws FormatterException {//
		return FormatUtils.getDate(timeStr, format).getTime();
	}

	/**
	 * 把 yyyy-MM-dd 类型的时间转化为毫秒数
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static long getTime(String date) {
		return getTime(date, yyyy_MM_dd);
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
	 * 判断是否是同一天
	 */
	public static boolean isSameDay(long d1, long d2) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String str1 = sdf.format(d1);
		String str2 = sdf.format(d2);
		if (str1.equals(str2)) {
			return true;
		}
		return false;
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
	 * 是否是同一天
	 * 
	 * @param d1
	 * @param standardTime
	 * @return
	 */
	public static boolean isSameDay(Date d1, Date standardTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date1Str = sdf.format(d1);
		String date2Str = sdf.format(standardTime);
		return date1Str.equals(date2Str);
	}

	public static String format(Date d) {
		if (d == null) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yy年MM月dd日");
		return sdf.format(d);
	}

	public static String format(Date d, String formatter) {
		if (d == null) {
			return "";
		}

		return FormatUtils.dateFormat(d, formatter);
	}

	public static String format(long t, String formatter) {
		Date d = new Date();
		d.setTime(t);
		return format(d, formatter);
	}

	/**
	 * 输出格式:中文-今天/昨天/前天
	 * 
	 * @param d
	 * @return
	 */
	public static String formatDate(Date d) {
		Date today = new Date();

		Date yesterday = new Date();
		yesterday.setTime(today.getTime() - 24 * 60 * 60 * 1000);

		Date beforeYesterday = new Date();
		beforeYesterday.setTime(today.getTime() - 48 * 60 * 60 * 1000);

		if (XTime.isSameDay(d, today)) {
			return "今天";
		} else if (XTime.isSameDay(d, yesterday)) {
			return "昨天";
		} else if (XTime.isSameDay(d, beforeYesterday)) {
			return "前天";
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
			return sdf.format(d);
		}
	}
}