package run.soeasy.framework.core.convert.strings;

import lombok.Data;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ReversibleConverter;
import run.soeasy.framework.core.convert.TypeDescriptor;

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
