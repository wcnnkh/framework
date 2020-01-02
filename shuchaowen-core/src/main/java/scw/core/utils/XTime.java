package scw.core.utils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import scw.util.CalendarUtils;

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
	 * 将时间格式和时间字符串值传入，获得时间戳<br>
	 * 举例时间格式 yyyy-MM-dd hh:mm:ss
	 * 
	 * @param timeStr
	 *            ,时间2010-09-12 00:00:00
	 * @param format
	 *            ,时间格式 yyyy-MM-dd hh:mm:ss
	 * @return
	 */
	public static long getTime(String timeStr, String format) throws FormatterException{//
		return FormatUtils.getDate(timeStr, format).getTime();
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
	 * 
	 * @param 单位
	 *            ：秒
	 * @return
	 */
	public static String formatSec(long l) {
		String time = new String();

		long t = l;

		long sec = t % 60;
		t -= sec;
		t /= 60;

		long minute = t % 60;
		t -= minute;
		t /= 60;

		long hour = t;

		DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
		df.applyPattern("0");
		time += (df.format(hour));

		df.applyPattern("00");
		time += (":" + df.format(minute));
		time += (":" + df.format(sec));

		return time;
	}

	/**
	 * 
	 * @param 单位
	 *            ：秒
	 * @return
	 */
	public static String formatMSec(long l) {
		String time = new String();

		long t = l;

		long msec = t % 1000;
		t -= msec;
		t /= 1000;

		long sec = t % 60;
		t -= sec;
		t /= 60;

		long minute = t % 60;
		t -= minute;
		t /= 60;

		long hour = t;

		DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
		df.applyPattern("0");
		time += (df.format(hour));

		df.applyPattern("00");
		time += (":" + df.format(minute));
		time += (":" + df.format(sec));

		df.applyPattern("000");
		time += (":" + df.format(msec));

		return time;
	}

	/**
	 * 将时间转换成 day:hour:minute:second
	 * 
	 * @param 单位
	 *            ：秒
	 * @return
	 */
	public static String formatDay(long l) {
		String time = new String();

		long t = l;

		long sec = t % 60;
		t -= sec;
		t /= 60;

		long minute = t % 60;
		t -= minute;
		t /= 60;

		long hour = t % 24;
		t -= hour;
		t /= 24;

		long day = t;

		DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
		df.applyPattern("0");
		time += (df.format(day));

		df.applyPattern("00");
		time += (":" + df.format(hour));
		time += (":" + df.format(minute));
		time += (":" + df.format(sec));

		return time;
	}

	/**
	 * 将时间转换成 day:hour:minute:second
	 * 
	 * @param 单位
	 *            ：秒
	 * @return
	 */
	public static String formatDayCN(long l) {
		String time = new String();

		long t = l;

		long sec = t % 60;
		t -= sec;
		t /= 60;

		long minute = t % 60;
		t -= minute;
		t /= 60;

		long hour = t % 24;
		t -= hour;
		t /= 24;

		long day = t;

		DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
		df.applyPattern("0");
		time += (df.format(day));
		time += ("天" + df.format(hour));
		time += ("小时" + df.format(minute));
		time += ("分" + df.format(sec));
		time += "秒";

		return time;
	}

	/**
	 * @param 单位
	 *            ：毫秒
	 * @return
	 */

	public static String convert(long l) {
		String time = new String();

		long t = l;
		long msec = t % 1000;
		t -= msec;
		t /= 1000;

		long sec = t % 60;
		t -= sec;
		t /= 60;

		long minute = t % 60;
		t -= minute;
		t /= 60;

		long hour = t % 24;
		t -= hour;
		t /= 24;

		long day = t % 24;

		if (day > 0) {
			time += ("" + day);
			time += "day";
		}
		if (day > 0 || hour > 0) {
			time += (" " + hour);
			time += "hour";
		}
		if (day > 0 || hour > 0 || minute > 0) {
			time += (" " + minute);
			time += "min";
		}
		if (day > 0 || hour > 0 || minute > 0 || sec > 0) {
			time += (" " + sec);
			time += "sec";
		}
		if (day > 0 || hour > 0 || minute > 0 || sec > 0 || msec >= 0) {
			time += (" " + msec);
			time += "msec";
		}

		return time;
	}

	/**
	 * 把 yyyy-MM-dd 类型的时间转化为毫秒数
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static long formatStr(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sdf.parse(date).getTime();
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 支持把 hh:mm:ss 格式的字符串，转换成秒为单位的long。 mm:ss 或者 ss 格式的字符串也可以
	 * 
	 * @param ss
	 * @return 单位:秒
	 */
	public static long format(String ss) {
		String[] tt = ss.split(":");
		int[] unit = new int[4];
		unit[0] = 60;
		unit[1] = 60;
		unit[2] = 60;
		unit[3] = 24;

		long total = 0;
		int j = 0;
		int x = 1;
		for (int i = tt.length - 1; i >= 0; i--, j++) {
			String t = tt[i];

			int time = Integer.parseInt(t);
			time *= x;
			total += time;
			x *= unit[j];
		}

		return total;
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
	 * 判断是否是今天
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

	public static String formatTime(long time) {
		Date d = new Date();
		d.setTime(time);
		return formatDate(d);
	}

	/**
	 * 得到指定时间点的时间戳<br>
	 * 举例: 如取今天凌晨1点10分20秒的时间戳 <br>
	 * getDayTs(System.currentTimeMillis(), "01:10:20")
	 */
	public static long getDayTs(long ts, String endStr) {
		long endTs = 0L;
		try {
			DateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
			String ds = ft.format(new Date(ts)) + " " + endStr;
			Date dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(ds);
			endTs = dt.getTime();
		} catch (Exception e) {
		}
		return endTs;
	}

	/**
	 * 获取某个日子对应的星期
	 * 
	 * @param dt
	 * @return
	 */
	public static int getWeek(Date dt) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int week = cal.get(Calendar.DAY_OF_WEEK);

		if (week == 1) {
			week = 8;
		}
		week--;
		return week;
	}

	/**
	 * 获取某一天(yyyyMMdd)
	 * 
	 * @param dt
	 * @return
	 */
	public static int getDay(Date dt) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String dayStr = sdf.format(dt);
		int day = Integer.parseInt(dayStr);
		return day;
	}

	public static int getTime(Date dt) {
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
		String dayStr = sdf.format(dt);
		int day = Integer.parseInt(dayStr);
		return day;
	}

	/**
	 * 获取年月
	 * 
	 * @param dt
	 * @return
	 */
	public static int getYearMonth(Date dt) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		String dayStr = sdf.format(dt);
		int yearMonth = Integer.parseInt(dayStr);
		return yearMonth;
	}

	/**
	 * 获取注册日期后count天的留存天数
	 * 
	 * @param regDay
	 * @param count
	 * @return
	 */
	public static int[] getRemainDays(int regDay, int count) {
		int[] remainDays = new int[count];
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Date date = sdf.parse("" + regDay);

			long time = date.getTime();
			for (int i = 0; i < count; i++) {
				date.setTime(time + 24 * 3600000L * i);
				int day = getDay(date);
				remainDays[i] = day;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return remainDays;
	}

	/**
	 * 获取注册日期后 n 天日期
	 * 
	 * @param regDay
	 * @param count
	 * @return
	 */
	public static int getConverDay(int regDay, int count) {
		int remainDays = 0;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Date date = sdf.parse("" + regDay);

			long time = date.getTime();
			date.setTime(time + 24 * 3600000L * count);
			int day = getDay(date);
			remainDays = day;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return remainDays;
	}

	/**
	 * 获取时间段内所有天数
	 * 
	 * @param startDay
	 * @param endDay
	 * @return
	 */
	public static int[] getDays(int startDay, int endDay) {
		int[] days = null;
		try {

			if (endDay == 0 || startDay == endDay) {
				days = new int[] { startDay };
				return days;
			}

			List<Integer> dayList = new ArrayList<Integer>();

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Date startDate = sdf.parse("" + startDay);

			dayList.add(startDay);

			boolean isEnd = false;
			while (!isEnd) {
				long startTime = startDate.getTime();
				startDate.setTime(startTime + 24 * 3600000L);
				int day = getDay(startDate);

				if (day == endDay) {
					isEnd = true;
				}
				dayList.add(day);
			}

			if (dayList.size() > 0) {
				days = new int[dayList.size()];
				for (int i = 0; i < dayList.size(); i++) {
					days[i] = dayList.get(i);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return days;
	}

	/**
	 * 获取到今天凌晨的时间间隔
	 * 
	 * @param time
	 * @return
	 */
	public static int getToTodayTimeInterval(long time) {
		double interval = Math.abs(XTime.getTodayBeginTime() - time);
		return (int) Math.ceil(interval / ONE_DAY);
	}
}