package io.basc.framework.util.codec.support;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import io.basc.framework.util.StringUtils;
import io.basc.framework.util.codec.Codec;
import io.basc.framework.util.codec.DecodeException;
import io.basc.framework.util.codec.EncodeException;

/**
 * 时间和字符串
 * 
 * @see SimpleDateFormat
 * @author wcnnkh
 *
 */
public class DateCodec implements Codec<Date, String> {
	private final Locale locale;
	private final TimeZone timeZone;
	private final DateFormatSymbols dateFormatSymbols;
	private final String pattern;

	public DateCodec(String pattern) {
		this(pattern, null, null, null);
	}

	public DateCodec(String pattern, TimeZone timeZone, Locale locale, DateFormatSymbols dateFormatSymbols) {
		this.locale = locale;
		this.timeZone = timeZone;
		this.dateFormatSymbols = dateFormatSymbols;
		this.pattern = pattern;
	}

	public Locale getLocale() {
		return locale;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public String getPattern() {
		return pattern;
	}

	public SimpleDateFormat getDateFormat() {
		SimpleDateFormat dateFormat = StringUtils.hasText(pattern)
				? (locale == null ? new SimpleDateFormat(pattern) : new SimpleDateFormat(pattern, locale))
				: new SimpleDateFormat();
		if (timeZone != null) {
			dateFormat.setTimeZone(timeZone);
		}

		if (dateFormatSymbols != null) {
			dateFormat.setDateFormatSymbols(dateFormatSymbols);
		}
		return dateFormat;
	}

	/**
	 * 将时间编码为字符串
	 */
	@Override
	public String encode(Date source) throws EncodeException {
		if (source == null) {
			return null;
		}
		return getDateFormat().format(source);
	}

	/**
	 * 将字符串解码为时间
	 */
	@Override
	public Date decode(String source) throws DecodeException {
		if (!StringUtils.hasText(source)) {
			return null;
		}

		try {
			return getDateFormat().parse(source);
		} catch (ParseException e) {
			throw new DecodeException(source, e);
		}
	}

	public long parse(String source) throws DecodeException {
		Date date = decode(source);
		return date == null ? 0L : date.getTime();
	}

	public String format(long source) throws EncodeException {
		Date d = new Date();
		d.setTime(source);
		return encode(d);
	}

	public boolean equals(Date source, Date target) {
		if (source == null) {
			return target == null;
		}

		if (target == null) {
			return source == null;
		}

		if (source == target) {
			return true;
		}

		DateFormat sdf = getDateFormat();
		String date1 = sdf.format(source);
		String date2 = sdf.format(target);
		return date1.equals(date2);
	}

	public boolean equals(long source, long target) {
		return equals(new Date(source), new Date(target));
	}
}
