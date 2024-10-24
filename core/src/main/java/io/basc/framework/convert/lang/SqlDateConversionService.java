package io.basc.framework.convert.lang;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConversionFailedException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.UnsupportedException;

public class SqlDateConversionService extends AbstractConversionService implements ConditionalConversionService {
	private static final Set<ConvertiblePair> CONVERIBLE_PAIRS = new HashSet<ConvertiblePair>(8);

	static {
		CONVERIBLE_PAIRS.add(new ConvertiblePair(Date.class, Object.class));
		CONVERIBLE_PAIRS.add(new ConvertiblePair(Time.class, Object.class));
		CONVERIBLE_PAIRS.add(new ConvertiblePair(Timestamp.class, Object.class));
		CONVERIBLE_PAIRS.add(new ConvertiblePair(java.sql.Date.class, Object.class));
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (source instanceof Time) {
			return sqlTimeToObject((Time) source, targetType.getType());
		}

		if (source instanceof Timestamp) {
			return sqlTimestampToObject((Timestamp) source, targetType.getType());
		}

		if (source instanceof java.sql.Date) {
			return sqlDateToObject((java.sql.Date) source, targetType.getType());
		}

		if (source instanceof Date) {
			return javaDateToObject((Date) source, targetType.getType());
		}

		throw new ConversionFailedException(sourceType, targetType, source, null);
	}

	private Object javaDateToObject(Date source, Class<?> targetType) {
		if (targetType == java.sql.Date.class) {
			return new java.sql.Date(source.getTime());
		} else if (targetType == Timestamp.class) {
			return new Timestamp(source.getTime());
		} else if (targetType == Time.class) {
			return new Time(source.getTime());
		}
		throw new UnsupportedException(targetType.getName());
	}

	private Object sqlDateToObject(java.sql.Date source, Class<?> targetType) {
		if (targetType == Date.class) {
			return new Date(source.getTime());
		} else if (targetType == Timestamp.class) {
			return new Timestamp(source.getTime());
		} else if (targetType == Time.class) {
			return new Time(source.getTime());
		}
		throw new UnsupportedException(targetType.getName());
	}

	private Object sqlTimeToObject(Time source, Class<?> targetType) {
		if (targetType == Date.class) {
			return new Date(source.getTime());
		} else if (targetType == Timestamp.class) {
			return new Timestamp(source.getTime());
		} else if (targetType == Time.class) {
			return new Time(source.getTime());
		}
		throw new UnsupportedException(targetType.getName());
	}

	private Object sqlTimestampToObject(Timestamp source, Class<?> targetType) {
		if (targetType == Date.class) {
			return new Date(source.getTime());
		} else if (targetType == Time.class) {
			return new Time(source.getTime());
		} else if (targetType == java.sql.Date.class) {
			return new java.sql.Date(source.getTime());
		}
		throw new UnsupportedException(targetType.getName());
	}

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return CONVERIBLE_PAIRS;
	}

	private boolean isTimeType(TypeDescriptor type) {
		return type.getType() == Date.class || type.getType() == java.sql.Date.class
				|| type.getType() == Timestamp.class || type.getType() == Time.class;
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (sourceType == null || targetType == null) {
			return false;
		}

		return isTimeType(sourceType) && isTimeType(targetType);
	}

}
