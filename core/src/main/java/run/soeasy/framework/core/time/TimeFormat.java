package run.soeasy.framework.core.time;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.codec.DecodeException;
import run.soeasy.framework.codec.EncodeException;
import run.soeasy.framework.core.convert.support.DataConverter;
import run.soeasy.framework.core.convert.value.TypedData;
import run.soeasy.framework.core.convert.value.TypedValue;

@Getter
public class TimeFormat extends DataConverter<Date, String> {
	public static final TimeFormat DATE = new TimeFormat("EEE MMM dd HH:mm:ss zzz yyyy");

	@NonNull
	private final String pattern;

	public TimeFormat(@NonNull String pattern) {
		super(Date.class, String.class);
		this.pattern = pattern;
	}

	public DateFormat getDateFormat() {
		return new SimpleDateFormat(pattern);
	}

	@Override
	public TypedData<String> encode(TypedData<Date> source) throws EncodeException {
		return TypedValue.of(format(source.get())).getAsData(String.class);
	}

	@Override
	public TypedData<Date> decode(TypedData<String> source) throws DecodeException {
		return TypedValue.of(parse(source.get())).getAsData(Date.class);
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
