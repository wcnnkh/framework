package run.soeasy.framework.core.time;

import java.util.Date;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.ConverterNotFoundException;
import run.soeasy.framework.core.convert.Converters;
import run.soeasy.framework.core.convert.TypeDescriptor;

public class TimeFormatter extends Converters {
	private static volatile TimeFormatter instance;

	public static TimeFormatter getInstance() {
		if (instance == null) {
			synchronized (TimeFormatter.class) {
				if (instance == null) {
					instance = new TimeFormatter();
					instance.register(Year.DEFAULT);
					instance.register(Month.DEFAULT);
					instance.register(Day.DEFAULT);
					instance.register(Hour.DEFAULT);
					instance.register(Minute.DEFAULT);
					instance.register(Second.DEFAULT);
					instance.register(Millisecond.DEFAULT);
					instance.register(TimeFormat.DATE);
				}
			}
		}
		return instance;
	}

	@Override
	public Object convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		for (Converter converter : this) {
			if (converter.canConvert(sourceTypeDescriptor, targetTypeDescriptor)) {
				try {
					return converter.convert(source, sourceTypeDescriptor, targetTypeDescriptor);
				} catch (ConversionException e) {
					// ignore
				}
			}
		}
		throw new ConverterNotFoundException(sourceTypeDescriptor, targetTypeDescriptor);
	}

	public static String format(Date date) throws ConversionException {
		return getInstance().convert(date, String.class);
	}

	public static String format(long time) throws ConversionException {
		return format(new Date(time));
	}

	public static Date parse(String formatDate) throws ConversionException {
		return getInstance().convert(formatDate, Date.class);
	}
}
