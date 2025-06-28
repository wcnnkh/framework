package run.soeasy.framework.core.convert.strings;

import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;

public class StringToFloatConverter implements StringConverter<Float> {
	public static StringToFloatConverter DEFAULT = new StringToFloatConverter();

	@Override
	public Float from(String source, TypeDescriptor sourceType, TypeDescriptor targetType) throws ConversionException {
		if (StringUtils.isEmpty(source)) {
			return null;
		}
		return Float.parseFloat(source);
	}

	@Override
	public String to(Float source, TypeDescriptor sourceType, TypeDescriptor targetType) throws ConversionException {
		return source == null ? null : Float.toString(source);
	}

}
