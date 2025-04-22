package run.soeasy.framework.core.convert.date;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.core.convert.ConditionalConversionService;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConversionFailedException;
import run.soeasy.framework.core.convert.ConvertiblePair;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.support.AbstractConversionService;
import run.soeasy.framework.core.convert.value.ValueAccessor;
import run.soeasy.framework.core.math.NumberUtils;
import run.soeasy.framework.core.time.TimeUtils;

@Data
@EqualsAndHashCode(callSuper = true)
public class DateFormatConversionService extends AbstractConversionService implements ConditionalConversionService {
	private static final Set<ConvertiblePair> CONVERTIBLE_PAIRS = new HashSet<>(4);

	static {
		CONVERTIBLE_PAIRS.add(new ConvertiblePair(String.class, Date.class));
		CONVERTIBLE_PAIRS.add(new ConvertiblePair(Date.class, String.class));
	}

	@NonNull
	private final DateCodecResolver dateCodecResolver;

	private boolean canConvert(Class<?> type) {
		return type == String.class || Date.class.isAssignableFrom(type) || NumberUtils.isNumber(type);
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (sourceType == null || targetType == null) {
			return false;
		}

		return (dateCodecResolver.resolveDateCodec(() -> targetType) != null
				|| dateCodecResolver.resolveDateCodec(() -> sourceType) != null) && canConvert(sourceType.getType())
				&& canConvert(targetType.getType());
	}

	@Override
	public Object convert(@NonNull ValueAccessor value, @NonNull TypeDescriptor targetType) throws ConversionException {
		Object source = value.get();
		if (source == null) {
			return null;
		}

		Codec<Date, String> sourceCodec = dateCodecResolver.resolveDateCodec(value);
		Codec<Date, String> targetCodec = dateCodecResolver.resolveDateCodec(() -> targetType);
		TypeDescriptor sourceType = value.getTypeDescriptor();
		if (sourceType.getType() == String.class) {
			if (targetType.getType() == Date.class) {
				return stringToDate((String) source, sourceType, targetType, sourceCodec, targetCodec);
			}

			if (NumberUtils.isNumber(targetType.getType())) {
				return stringToNumber((String) source, sourceType, targetType, sourceCodec, targetCodec);
			}
		}

		if (sourceType.getType() == Date.class) {
			if (targetType.getType() == String.class) {
				return dateToString((Date) source, sourceType, targetType, sourceCodec, targetCodec);
			}

			if (NumberUtils.isNumber(targetType.getType())) {
				return dateToNumber((Date) source, sourceType, targetType);
			}
		}

		if (NumberUtils.isNumber(sourceType.getType())) {
			if (targetType.getType() == String.class) {
				return numberToString(source, sourceType, targetType, sourceCodec, targetCodec);
			}

			if (targetType.getType() == Date.class) {
				return numberToString(source, sourceType, targetType, sourceCodec, targetCodec);
			}

			if (NumberUtils.isNumber(targetType.getType())) {
				return ValueAccessor.of(source).getAsObject(targetType);
			}
		}

		if (!getConversionService().canConvert(sourceType, TypeDescriptor.valueOf(Date.class))) {
			throw new ConversionFailedException(sourceType, targetType, source, null);
		}

		Date date = (Date) getConversionService().convert(source, sourceType, TypeDescriptor.valueOf(Date.class));
		return convert(date, sourceType.narrow(date), targetType);
	}

	private Date stringToDate(String source, TypeDescriptor sourceType, TypeDescriptor targetType,
			Codec<Date, String> sourceCodec, Codec<Date, String> targetCodec) {
		if (sourceCodec == null) {
			if (targetCodec == null) {
				return TimeUtils.convert(source);
			} else {
				return targetCodec.decode(source);
			}
		} else {
			if (targetCodec == null) {
				return sourceCodec.decode(source);
			} else {
				source = sourceCodec.encode(sourceCodec.decode(source));
				return targetCodec.decode(source);
			}
		}
	}

	private Object stringToNumber(String source, TypeDescriptor sourceType, TypeDescriptor targetType,
			Codec<Date, String> sourceCodec, Codec<Date, String> targetCodec) {
		Date date = stringToDate(source, sourceType, targetType.narrow(Date.class), sourceCodec, targetCodec);
		return dateToNumber(date, sourceType.narrow(date), targetType);
	}

	private String dateToString(Date source, TypeDescriptor sourceType, TypeDescriptor targetType,
			Codec<Date, String> sourceCodec, Codec<Date, String> targetCodec) {
		if (targetCodec == null) {
			if (sourceCodec == null) {
				return String.valueOf(source);
			} else {
				return sourceCodec.encode(source);
			}
		} else {
			if (sourceCodec != null) {
				source = sourceCodec.decode(sourceCodec.encode(source));
			}
			return targetCodec.encode(source);
		}
	}

	private Object dateToNumber(Date source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		return ValueAccessor.of(source.getTime()).getAsObject(targetType);
	}

	private Date numberToDate(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		long time = ValueAccessor.of(source).getAsLong();
		return new Date(time);
	}

	private String numberToString(Object source, TypeDescriptor sourceType, TypeDescriptor targetType,
			Codec<Date, String> sourceCodec, Codec<Date, String> targetCodec) {
		Date date = numberToDate(source, sourceType, targetType);
		if (targetCodec == null) {
			if (sourceCodec == null) {
				return String.valueOf(date);
			} else {
				return sourceCodec.encode(date);
			}
		} else {
			if (sourceCodec == null) {
				return targetCodec.encode(date);
			} else {
				date = sourceCodec.decode(sourceCodec.encode(date));
				return targetCodec.encode(date);
			}
		}
	}

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return CONVERTIBLE_PAIRS;
	}
}
