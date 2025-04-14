package run.soeasy.framework.core.time;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.TimeZone;

import run.soeasy.framework.codec.DecodeException;
import run.soeasy.framework.codec.Decoder;
import run.soeasy.framework.codec.support.DateCodec;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.collection.ArrayUtils;
import run.soeasy.framework.core.convert.ConversionException;

public class TimeUtils extends DateCodec {
	public static final TimeUtils YEAR = new TimeUtils(Calendar.DAY_OF_YEAR, "yyyy");
	public static final TimeUtils MONTH = new TimeUtils(Calendar.DAY_OF_MONTH, "yyyy-MM");
	public static final TimeUtils WEEK = new TimeUtils(Calendar.DAY_OF_WEEK, "yyyy-MM-dd E");
	public static final TimeUtils DAY = new TimeUtils(Calendar.HOUR_OF_DAY, "yyyy-MM-dd");
	public static final TimeUtils HOUR = new TimeUtils(Calendar.MINUTE, "yyyy-MM-dd HH");
	public static final TimeUtils MINUTE = new TimeUtils(Calendar.SECOND, "yyyy-MM-dd HH:mm");
	public static final TimeUtils SECOND = new TimeUtils(Calendar.MILLISECOND, "yyyy-MM-dd HH:mm:ss");
	public static final TimeUtils MILLISECOND = new TimeUtils(-1, "yyyy-MM-dd HH:mm:ss,SSS");

	/**
	 * 日期类的默认实现，会丢弃毫秒
	 * 
	 * @see Date#parse(String)
	 * @see Date#toString()
	 */
	public static final TimeUtils DATE = new TimeUtils(-1, "EEE MMM dd HH:mm:ss zzz yyyy", null, Locale.US, null);

	/**
	 * -1表示无效的字段
	 */
	private final int field;

	public TimeUtils(int field, String pattern) {
		super(pattern);
		this.field = field;
	}

	/**
	 * @param field             -1表示无效的字段
	 * @param pattern
	 * @param timeZone
	 * @param locale
	 * @param dateFormatSymbols
	 */
	public TimeUtils(int field, String pattern, TimeZone timeZone, Locale locale, DateFormatSymbols dateFormatSymbols) {
		super(pattern, timeZone, locale, dateFormatSymbols);
		this.field = field;
	}

	/**
	 * @return -1表示无效的字段
	 */
	public int getField() {
		return this.field;
	}

	public void setMin(Calendar calendar) {
		setTimeBoundary(calendar, field, true);
	}

	public void setMax(Calendar calendar) {
		setTimeBoundary(calendar, field, false);
	}

	public Calendar getMinCalendar(long source) {
		Calendar calendar = getCalendar(getTimeZone(), getLocale());
		calendar.setTimeInMillis(source);
		setMin(calendar);
		return calendar;
	}

	public Calendar getMaxCalendar(long source) {
		Calendar calendar = getCalendar(getTimeZone(), getLocale());
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
		Calendar calendar = getCalendar(getTimeZone(), getLocale());
		setMin(calendar);
		return calendar;
	}

	public Calendar getMaxCalendar() {
		Calendar calendar = getCalendar(getTimeZone(), getLocale());
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

	public static Calendar getCalendar(TimeZone zone, Locale locale) {
		if (zone == null) {
			if (locale == null) {
				return Calendar.getInstance();
			} else {
				return Calendar.getInstance(locale);
			}
		} else {
			if (locale == null) {
				return Calendar.getInstance(zone);
			} else {
				return Calendar.getInstance(zone, locale);
			}
		}
	}

	/**
	 * 进行默认转换，会对多种格式进行尝试
	 * 
	 * @param source
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Date convert(String source) {
		return convert(source, new Decoder[0]);
	}

	@SafeVarargs
	public static Date convert(String source, Decoder<String, ? extends Date>... decoders) {
		if (decoders == null || decoders.length == 0) {
			return convert(source, DATE, MILLISECOND, SECOND, MINUTE, HOUR, DAY, WEEK, MONTH, YEAR);
		} else if (decoders.length == 1) {
			return decoders[0].decode(source);
		} else {
			DecodeException error = null;
			for (Decoder<String, ? extends Date> decoder : decoders) {
				try {
					return decoder.decode(source);
				} catch (DecodeException e) {
					if (error == null) {
						error = e;
					} else {
						error.addSuppressed(e);
					}
				}
			}
			throw new DecodeException(source, error);
		}
	}

	public static Date parse(String source, String... patterns) throws ConversionException {
		return parse(source, null, null, patterns);
	}

	public static Date parse(String source, TimeZone timeZone, Locale locale, String... patterns)
			throws IllegalFormatException {
		if (StringUtils.isEmpty(source)) {
			return null;
		}

		if (ArrayUtils.isEmpty(patterns)) {
			return convert(source);
		} else if (patterns.length == 1) {
			return new DateCodec(patterns[0], timeZone, locale, null).decode(source);
		} else {
			DecodeException error = null;
			for (String pattern : patterns) {
				try {
					return new DateCodec(pattern, timeZone, locale, null).decode(source);
				} catch (DecodeException e) {
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
	 * @param source
	 * @param pattern
	 * @return
	 */
	public static String format(Date source, String pattern) {
		return format(source, null, null, pattern);
	}

	public static String format(Date source, TimeZone zone, Locale locale, String pattern) {
		if (source == null) {
			return null;
		}

		return new DateCodec(pattern, zone, locale, null).encode(source);
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

	public static String format(long source, TimeZone zone, Locale locale, String pattern) {
		Date d = new Date();
		d.setTime(source);
		return format(d, zone, locale, pattern);
	}

	/**
	 * 将指定格式的字段串转换为时间戳
	 * 
	 * @param source
	 * @param patterns
	 * @return
	 */
	public static long getTime(String source, String... patterns) throws ConversionException {
		return getTime(source, null, null, patterns);
	}

	public static long getTime(String source, TimeZone zone, Locale locale, String... patterns) {
		Date date = parse(source, zone, locale, patterns);
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
		return DAY.equals(System.currentTimeMillis(), source);
	}
}
