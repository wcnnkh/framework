package io.basc.framework.convert.lang;

import java.util.Date;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConversionFailedException;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.annotation.DateFormat;
import io.basc.framework.util.NumberUtils;
import io.basc.framework.util.TimeUtils;
import io.basc.framework.value.AnyValue;

public class DateFormatConversionService implements ConversionService {

	private boolean canConvert(Class<?> type) {
		return type == String.class || Date.class.isAssignableFrom(type) || NumberUtils.isNumber(type);
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (sourceType == null || targetType == null) {
			return false;
		}

		return (targetType.isAnnotationPresent(DateFormat.class) || sourceType.isAnnotationPresent(DateFormat.class))
				&& canConvert(sourceType.getType()) && canConvert(targetType.getType());
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (source == null || sourceType == null) {
			return null;
		}

		if (sourceType.getType() == String.class) {
			if (targetType.getType() == Date.class) {
				return stringToDate((String) source, sourceType, targetType);
			}

			if (NumberUtils.isNumber(targetType.getType())) {
				return stringToNumber((String) source, sourceType, targetType);
			}
		}

		if (sourceType.getType() == Date.class) {
			if (targetType.getType() == String.class) {
				return dateToString((Date) source, sourceType, targetType);
			}

			if (NumberUtils.isNumber(targetType.getType())) {
				return dateToNumber((Date) source, sourceType, targetType);
			}
		}

		if (NumberUtils.isNumber(sourceType.getType())) {
			if (targetType.getType() == String.class) {
				return numberToString(source, sourceType, targetType);
			}

			if (targetType.getType() == Date.class) {
				return numberToString(source, sourceType, targetType);
			}

			if (NumberUtils.isNumber(targetType.getType())) {
				return new AnyValue(source).getAsObject(targetType);
			}
		}

		throw new ConversionFailedException(sourceType, targetType, source, null);
	}

	private Date stringToDate(String source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		DateFormat sourceFormat = sourceType.getAnnotation(DateFormat.class);
		DateFormat targetFormat = sourceType.getAnnotation(DateFormat.class);
		if (sourceFormat == null) {
			if (targetFormat == null) {
				return TimeUtils.parse(source);
			} else {
				return TimeUtils.parse(source, targetFormat.value());
			}
		} else {
			if (targetFormat == null) {
				return TimeUtils.parse(source, sourceFormat.value());
			} else {
				source = TimeUtils.format(TimeUtils.parse(source, sourceFormat.value()), sourceFormat.value());
				return TimeUtils.parse(source, targetFormat.value());
			}
		}
	}

	private Object stringToNumber(String source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		Date date = stringToDate(source, sourceType, targetType.narrow(Date.class));
		return dateToNumber(date, sourceType.narrow(date), targetType);
	}

	private String dateToString(Date source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		DateFormat sourceFormat = sourceType.getAnnotation(DateFormat.class);
		DateFormat dateFormat = targetType.getAnnotation(DateFormat.class);
		if (dateFormat == null) {
			if (sourceFormat == null) {
				return String.valueOf(source);
			} else {
				return TimeUtils.format(source, sourceFormat.value());
			}
		} else {
			if (sourceFormat != null) {
				source = TimeUtils.parse(TimeUtils.format(source, sourceFormat.value()), sourceFormat.value());
			}
			return TimeUtils.format(source, dateFormat.value());
		}
	}

	private Object dateToNumber(Date source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		return new AnyValue(source.getTime()).getAsObject(targetType);
	}

	private Date numberToDate(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		AnyValue anyValue = new AnyValue(source);
		long time = anyValue.getAsLongValue();
		return new Date(time);
	}

	private String numberToString(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		Date date = numberToDate(source, sourceType, targetType);
		DateFormat sourceFormat = sourceType.getAnnotation(DateFormat.class);
		DateFormat targetFormat = targetType.getAnnotation(DateFormat.class);
		if (targetFormat == null) {
			if (sourceFormat == null) {
				return String.valueOf(date);
			} else {
				return TimeUtils.format(date, sourceFormat.value());
			}
		} else {
			if (sourceFormat == null) {
				return TimeUtils.format(date, targetFormat.value());
			} else {
				date = TimeUtils.parse(TimeUtils.format(date, sourceFormat.value()), sourceFormat.value());
				return TimeUtils.format(date, targetFormat.value());
			}
		}
	}
}
