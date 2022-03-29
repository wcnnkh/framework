package io.basc.framework.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import io.basc.framework.convert.ConversionException;

public class TimeUtils {
	public static final TimeUtils YEAR = new TimeUtils(Calendar.DAY_OF_YEAR, "yyyy");
	public static final TimeUtils MONTH = new TimeUtils(Calendar.DAY_OF_MONTH, "yyyy-MM");
	public static final TimeUtils WEEK = new TimeUtils(Calendar.DAY_OF_WEEK, "yyyy-MM-dd E");
	public static final TimeUtils DAY = new TimeUtils(Calendar.HOUR_OF_DAY, "yyyy-MM-dd");
	public static final TimeUtils HOUR = new TimeUtils(Calendar.MINUTE, "yyyy-MM-dd HH");
	public static final TimeUtils MINUTE = new TimeUtils(Calendar.SECOND, "yyyy-MM-dd HH:mm");
	public static final TimeUtils SECOND = new TimeUtils(Calendar.MILLISECOND, "yyyy-MM-dd HH:mm:ss");
	public static final TimeUtils MILLISECOND = new TimeUtils(-1, "yyyy-MM-dd HH:mm:ss,SSS");

	private final int field;
	private final String pattern;

	public TimeUtils(int field, String pattern) {
		this.field = field;
		this.pattern = pattern;
	}

	public int getField() {
		return this.field;
	}

	public String getPattern() {
		return this.pattern;
	}

	public void setMin(Calendar calendar) {
		setTimeBoundary(calendar, field, true);
	}

	public void setMax(Calendar calendar) {
		setTimeBoundary(calendar, field, false);
	}

	public Calendar getMinCalendar(long source) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(source);
		setMin(calendar);
		return calendar;
	}

	public Calendar getMaxCalendar(long source) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(source);
		setMax(calendar);
		return calendar;
	}

	public Date getMinDate(long source) {
		return getMinCalendar(source).getTime();
	}

	public Date getMaxDate(long source) {
		return getMaxCalendar(source).getTime();
	}

	public long getMinTime(long source) {
		return getMinDate(source).getTime();
	}

	public long getMaxTime(long source) {
		return getMinDate(source).getTime();
	}

	public Calendar getMinCalendar() {
		Calendar calendar = Calendar.getInstance();
		setMin(calendar);
		return calendar;
	}

	public Calendar getMaxCalendar() {
		Calendar calendar = Calendar.getInstance();
		setMax(calendar);
		return calendar;
	}

	public Date getMinDate() {
		return getMinCalendar().getTime();
	}

	public Date getMaxDate() {
		return getMaxCalendar().getTime();
	}

	public long getMinTime() {
		return getMinDate().getTime();
	}

	public long getMaxTime() {
		return getMinDate().getTime();
	}

	public Date parse(String source) {
		return parse(source, this.pattern);
	}

	public long getTime(String source) {
		return getTime(source, this.pattern);
	}

	public String format(Date source) {
		return format(source, pattern);
	}

	public String format(long source) {
		return format(source, pattern);
	}

	public boolean contains(Date source, Date target) {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(target != null, "target");
		SimpleDateFormat sdf = new SimpleDateFormat(this.pattern);
		String date1 = sdf.format(source);
		String date2 = sdf.format(target);
		return date1.equals(date2);
	}

	public boolean contains(long source, long target) {
		return contains(new Date(source), new Date(target));
	}

	/**
	 * 进行默认转换，会对多种格式进行尝试
	 * 
	 * @param source
	 * @return
	 */
	public static Date convert(String source) {
		return parse(source, new String[0]);
	}

	public static String toString(Date source) {
		if (source == null) {
			return null;
		}

		return new SimpleDateFormat().format(source);
	}

	public static Date parse(String source, String... patterns) throws ConversionException {
		if (StringUtils.isEmpty(source)) {
			return null;
		}

		if (ArrayUtils.isEmpty(patterns)) {
			SimpleDateFormat format = new SimpleDateFormat();
			try {
				return format.parse(source);
			} catch (ParseException e) {
				for (TimeUtils util : Arrays.asList(MILLISECOND, SECOND, MINUTE, HOUR, DAY, WEEK, MONTH, YEAR)) {
					try {
						return util.parse(source);
					} catch (ConversionException e1) {
					}
				}
			}
			throw new ConversionException(source);
		} else if (patterns.length == 1) {
			try {
				return new SimpleDateFormat(patterns[0]).parse(source);
			} catch (ParseException e) {
				throw new ConversionException("source=" + source + ", pattern=" + patterns[0], e);
			}
		} else {
			Throwable error = null;
			for (String pattern : patterns) {
				try {
					return new SimpleDateFormat(pattern).parse(source);
				} catch (ParseException e) {
					if (error == null) {
						error = e;
					} else {
						error.addSuppressed(e);
					}
				}
			}
			throw new ConversionException("source=" + source + ", patterns=" + Arrays.toString(patterns), error);
		}
	}

	/**
	 * 将时间格式化为字符串
	 * 
	 * @see DateFormat#format(Date)
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String format(Date source, String pattern) {
		if (source == null) {
			return null;
		}

		Assert.requiredArgument(StringUtils.hasText(pattern), "pattern");
		return new SimpleDateFormat(pattern).format(source);
	}

	/**
	 * 将时间戳转换为指定格式的字符串
	 * 
	 * @param source
	 * @param pattern
	 * @return
	 */
	public static String format(long source, String pattern) {
		Date d = new Date();
		d.setTime(source);
		return format(d, pattern);
	}

	/**
	 * 将指定格式的字段串转换为时间戳
	 * 
	 * @param source
	 * @param patterns
	 * @return
	 */
	public static long getTime(String source, String... patterns) throws ConversionException {
		Date date = parse(source, patterns);
		return date == null ? 0L : date.getTime();
	}

	/**
	 * Parse the given {@code timeZoneString} value into a {@link TimeZone}.
	 * 
	 * @param timeZoneString the time zone {@code String}, following
	 *                       {@link TimeZone#getTimeZone(String)} but throwing
	 *                       {@link IllegalArgumentException} in case of an invalid
	 *                       time zone specification
	 * @return a corresponding {@link TimeZone} instance
	 * @throws IllegalArgumentException in case of an invalid time zone
	 *                                  specification
	 */
	public static TimeZone parseTimeZoneString(String timeZoneString) {
		TimeZone timeZone = TimeZone.getTimeZone(timeZoneString);
		if ("GMT".equals(timeZone.getID()) && !timeZoneString.startsWith("GMT")) {
			// We don't want that GMT fallback...
			throw new IllegalArgumentException("Invalid time zone specification '" + timeZoneString + "'");
		}
		return timeZone;
	}

	/**
	 * 转换为默认的Instant
	 * 
	 * @see LocalDateTime#toInstant(java.time.ZoneOffset)
	 * @see OffsetDateTime#getOffset()
	 * @param localDateTime
	 * @return
	 */
	public static Instant toInstant(LocalDateTime localDateTime) {
		Assert.requiredArgument(localDateTime != null, "localDateTime");
		return localDateTime.toInstant(OffsetDateTime.now().getOffset());
	}

	/**
	 * 设置边界时间
	 * 
	 * @param calendar
	 * @param field
	 * @param min      是否是边界的最小时间 true为最小 false为最大
	 */
	public static void setTimeBoundary(Calendar calendar, int field, boolean min) {
		if (field < Calendar.ERA) {
			return;
		}

		Assert.isTrue(field <= Calendar.MILLISECOND, "field needs to be less than Calendar#MILLISECOND");
		for (int i = Calendar.MILLISECOND; i >= field && i >= Calendar.MINUTE; i--) {
			setTimeBoundaryActual(calendar, i, min);
		}

		if (field <= Calendar.DAY_OF_WEEK_IN_MONTH) {
			setTimeBoundaryActual(calendar, Calendar.HOUR_OF_DAY, min);
		}

		if (field <= Calendar.MONTH) {
			setTimeBoundaryActual(calendar, Calendar.DAY_OF_MONTH, min);
		}

		if (field <= Calendar.YEAR) {
			setTimeBoundaryActual(calendar, Calendar.MONTH, min);
		}

		if (field < Calendar.MINUTE) {
			setTimeBoundaryActual(calendar, field, min);
		}
	}

	private static void setTimeBoundaryActual(Calendar calendar, int field, boolean min) {
		calendar.set(field, min ? calendar.getActualMinimum(field) : calendar.getActualMaximum(field));
	}

	/**
	 * 获取今日的凌晨0:00
	 * 
	 * @return
	 */
	public static long getTodayBeginTime() {
		return DAY.getMinTime();
	}

	/**
	 * 判断是否是今天
	 */
	public static boolean isToday(long source) {
		return DAY.contains(System.currentTimeMillis(), source);
	}
}
