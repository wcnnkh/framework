package run.soeasy.framework.core.convert.support.strings;

import lombok.Data;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ReversibleConverter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.util.StringUtils;

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
	public String reverseConvert(Integer source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (source == null) {
			return null;
		}

		return unsigned ? Integer.toUnsignedString(source, radix) : Integer.toString(source, radix);
	}

}
