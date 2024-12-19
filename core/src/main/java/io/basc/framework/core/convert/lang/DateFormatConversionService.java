package io.basc.framework.core.convert.lang;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.ConversionFailedException;
import io.basc.framework.core.convert.ConvertiblePair;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.annotation.DateFormat;
import io.basc.framework.core.convert.config.ConditionalConversionService;
import io.basc.framework.util.NumberUtils;
import io.basc.framework.util.TimeUtils;

public class DateFormatConversionService extends AbstractConversionService implements ConditionalConversionService {
	private static final Set<ConvertiblePair> CONVERTIBLE_PAIRS = new HashSet<>(4);

	static {
		CONVERTIBLE_PAIRS.add(new ConvertiblePair(String.class, Date.class));
		CONVERTIBLE_PAIRS.add(new ConvertiblePair(Date.class, String.class));
	}

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
				return Value.of(source).getAsObject(targetType);
			}
		}

		if (!getConversionService().canConvert(sourceType, TypeDescriptor.valueOf(Date.class))) {
			throw new ConversionFailedException(sourceType, targetType, source, null);
		}

		Date date = (Date) getConversionService().convert(source, sourceType, TypeDescriptor.valueOf(Date.class));
		return convert(date, sourceType.narrow(date), targetType);
	}

	private Date stringToDate(String source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		DateFormat sourceFormat = sourceType.getAnnotation(DateFormat.class);
		DateFormat targetFormat = targetType.getAnnotation(DateFormat.class);
		if (sourceFormat == null) {
			if (targetFormat == null) {
				return TimeUtils.convert(source);
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
		DateFormat targetFormat = targetType.getAnnotation(DateFormat.class);
		if (targetFormat == null) {
			if (sourceFormat == null) {
				return String.valueOf(source);
			} else {
				return TimeUtils.format(source, sourceFormat.value());
			}
		} else {
			if (sourceFormat != null) {
				source = TimeUtils.parse(TimeUtils.format(source, sourceFormat.value()), sourceFormat.value());
			}
			return TimeUtils.format(source, targetFormat.value());
		}
	}

	private Object dateToNumber(Date source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		return Value.of(source.getTime()).getAsObject(targetType);
	}

	private Date numberToDate(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		long time = Value.of(source).getAsLong();
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

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return CONVERTIBLE_PAIRS;
	}
}
