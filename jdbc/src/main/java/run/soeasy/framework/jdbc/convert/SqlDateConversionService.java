package run.soeasy.framework.jdbc.convert;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConversionFailedException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.TypeMapping;
import run.soeasy.framework.core.convert.support.AbstractConditionalConverter;

public class SqlDateConversionService extends AbstractConditionalConverter {
	private static final Set<TypeMapping> CONVERIBLE_PAIRS = new HashSet<TypeMapping>(8);

	static {
		CONVERIBLE_PAIRS.add(new TypeMapping(Date.class, Object.class));
		CONVERIBLE_PAIRS.add(new TypeMapping(Time.class, Object.class));
		CONVERIBLE_PAIRS.add(new TypeMapping(Timestamp.class, Object.class));
		CONVERIBLE_PAIRS.add(new TypeMapping(java.sql.Date.class, Object.class));
	}

	@Override
	public Object convert(Object source, @NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType)
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
		throw new UnsupportedOperationException(targetType.getName());
	}

	private Object sqlDateToObject(java.sql.Date source, Class<?> targetType) {
		if (targetType == Date.class) {
			return new Date(source.getTime());
		} else if (targetType == Timestamp.class) {
			return new Timestamp(source.getTime());
		} else if (targetType == Time.class) {
			return new Time(source.getTime());
		}
		throw new UnsupportedOperationException(targetType.getName());
	}

	private Object sqlTimeToObject(Time source, Class<?> targetType) {
		if (targetType == Date.class) {
			return new Date(source.getTime());
		} else if (targetType == Timestamp.class) {
			return new Timestamp(source.getTime());
		} else if (targetType == Time.class) {
			return new Time(source.getTime());
		}
		throw new UnsupportedOperationException(targetType.getName());
	}

	private Object sqlTimestampToObject(Timestamp source, Class<?> targetType) {
		if (targetType == Date.class) {
			return new Date(source.getTime());
		} else if (targetType == Time.class) {
			return new Time(source.getTime());
		} else if (targetType == java.sql.Date.class) {
			return new java.sql.Date(source.getTime());
		}
		throw new UnsupportedOperationException(targetType.getName());
	}

	@Override
	public Set<TypeMapping> getConvertibleTypeMappings() {
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
