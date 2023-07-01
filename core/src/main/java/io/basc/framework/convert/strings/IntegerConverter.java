package io.basc.framework.convert.strings;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ReversibleConverter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.StringUtils;
import lombok.Data;

@Data
public class IntegerConverter implements ReversibleConverter<String, Integer, ConversionException> {
	private int radix = 10;
	private boolean unsigned = false;

	@Override
	public Integer convert(String source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (StringUtils.isEmpty(source)) {
			return targetType.isPrimitive() ? 0 : null;
		}

		return unsigned ? Integer.parseUnsignedInt(source, radix) : Integer.valueOf(source, radix);
	}

	@Override
	public String invert(Integer source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (source == null) {
			return null;
		}

		return unsigned ? Integer.toUnsignedString(source, radix) : Integer.toString(source, radix);
	}

}
