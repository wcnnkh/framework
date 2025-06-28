package run.soeasy.framework.core.time;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.strings.StringConverter;

/**
 * 日期转换
 * 
 * @author soeasy.run
 *
 */
@Getter
public class TimeFormat implements StringConverter<Date> {
	public static final TimeFormat DATE = new TimeFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);

	private final Locale locale;
	@NonNull
	private final String pattern;

	public TimeFormat(@NonNull String pattern) {
		this(pattern, null);
	}

	public TimeFormat(@NonNull String pattern, Locale locale) {
		this.pattern = pattern;
		this.locale = locale;
	}

	public final String format(Date source) {
		DateFormat dateFormat = getDateFormat();
		return dateFormat.format(source);
	}

	public final String format(long milliseconds) {
		return format(new Date(milliseconds));
	}

	@Override
	public Date from(String source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		return parse(source);
	}

	public DateFormat getDateFormat() {
		return locale == null ? new SimpleDateFormat(pattern) : new SimpleDateFormat(pattern, locale);
	}

	public final Date parse(String source) throws ConversionException {
		DateFormat dateFormat = getDateFormat();
		try {
			return dateFormat.parse(source);
		} catch (ParseException e) {
			throw new ConversionException(e);
		}
	}

	@Override
	public String to(Date source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		return format(source);
	}
}
