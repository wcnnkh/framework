package io.basc.framework.convert.strings;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ReversibleConverter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.StringUtils;
import lombok.Data;

@Data
public class LongConverter implements ReversibleConverter<String, Long, ConversionException> {
	private int radix = 10;
	private boolean unsigned = false;

	@Override
	public Long convert(String source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (StringUtils.isEmpty(source)) {
			return targetType.isPrimitive() ? 0L : null;
		}
		return unsigned ? Long.parseUnsignedLong(source, radix) : Long.parseLong(source, radix);
	}

	@Override
	public String invert(Long source, TypeDescriptor sourceType, TypeDescriptor targetType) throws ConversionException {
		if (source == null) {
			return null;
		}

		return unsigned ? Long.toUnsignedString(source, radix) : Long.toString(source, radix);
	}

}
