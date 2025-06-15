package run.soeasy.framework.core.time;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConversionFailedException;
import run.soeasy.framework.core.convert.ReversibleConverter;
import run.soeasy.framework.core.convert.TypeDescriptor;

@RequiredArgsConstructor
@Getter
public class TimeFormat implements ReversibleConverter<Date, String> {
	@NonNull
	private final String pattern;

	public DateFormat getDateFormat() {
		return new SimpleDateFormat(pattern);
	}

	@Override
	public String convert(Date source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		DateFormat dateFormat = getDateFormat();
		return dateFormat.format(source);
	}

	@Override
	public Date reverseConvert(String source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		DateFormat dateFormat = getDateFormat();
		try {
			return dateFormat.parse(source);
		} catch (ParseException e) {
			throw new ConversionFailedException(sourceTypeDescriptor, targetTypeDescriptor, source, e);
		}
	}

}
