package run.soeasy.framework.core.convert.strings;

import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ReversibleConverter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.strings.StringUtils;

public class FloatConverter implements ReversibleConverter<String, Float, ConversionException> {

	@Override
	public Float convert(String source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (StringUtils.isEmpty(source)) {
			return targetType.isPrimitive() ? 0f : null;
		}
		return Float.parseFloat(source);
	}

	@Override
	public String reverseConvert(Float source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return source == null ? null : source.toString();
	}

}
