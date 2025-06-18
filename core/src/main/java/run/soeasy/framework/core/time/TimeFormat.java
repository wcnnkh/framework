package run.soeasy.framework.core.time;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.codec.DecodeException;
import run.soeasy.framework.codec.EncodeException;
import run.soeasy.framework.core.convert.support.DataConverter;
import run.soeasy.framework.core.convert.value.TypedData;

@Getter
public class TimeFormat extends DataConverter<Date, String> {
	public static final TimeFormat DATE = new TimeFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);

	@NonNull
	private final String pattern;
	private final Locale locale;

	public TimeFormat(@NonNull String pattern) {
		this(pattern, null);
	}

	public TimeFormat(@NonNull String pattern, Locale locale) {
		super(Date.class, String.class);
		this.pattern = pattern;
		this.locale = locale;
	}

	public DateFormat getDateFormat() {
		return locale == null ? new SimpleDateFormat(pattern) : new SimpleDateFormat(pattern, locale);
	}

	@Override
	public TypedData<String> encode(TypedData<Date> source) throws EncodeException {
		return TypedData.forValue(format(source.get()));
	}

	@Override
	public TypedData<Date> decode(TypedData<String> source) throws DecodeException {
		return TypedData.forValue(parse(source.get()));
	}

	public String format(Date source) throws EncodeException {
		DateFormat dateFormat = getDateFormat();
		return dateFormat.format(source);
	}

	public Date parse(String source) throws DecodeException {
		DateFormat dateFormat = getDateFormat();
		try {
			return dateFormat.parse(source);
		} catch (ParseException e) {
			throw new DecodeException(e);
		}
	}

	public String format(long time) throws EncodeException {
		return format(new Date(time));
	}
}
