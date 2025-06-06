package run.soeasy.framework.core.convert.strings;

import lombok.Data;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ReversibleConverter;
import run.soeasy.framework.core.convert.TypeDescriptor;

@Data
public class LongConverter implements ReversibleConverter<String, Long> {
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
	public String reverseConvert(Long source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (source == null) {
			return null;
		}

		return unsigned ? Long.toUnsignedString(source, radix) : Long.toString(source, radix);
	}

}
