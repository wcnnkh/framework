package io.basc.framework.core.convert.strings;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.ReversibleConverter;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.util.StringUtils;
import lombok.Data;

@Data
public class ShortConverter implements ReversibleConverter<String, Short, ConversionException> {
	private int radix = 10;

	@Override
	public Short convert(String source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (StringUtils.isEmpty(source)) {
			return targetType.isPrimitive() ? (short) 0 : null;
		}
		return Short.parseShort(source, radix);
	}

	@Override
	public String reverseConvert(Short source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return source == null ? null : source.toString();
	}

}
