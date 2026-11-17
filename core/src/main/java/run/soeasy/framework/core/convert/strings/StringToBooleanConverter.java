package run.soeasy.framework.core.convert.strings;

import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.strings.StringUtils;

public class StringToBooleanConverter implements StringConverter<Boolean> {
	public static StringToBooleanConverter DEFAULT = new StringToBooleanConverter();

	public Boolean from(String source, TypeDescriptor sourceType, TypeDescriptor targetType) throws ConversionException {
		return StringUtils.isEmpty(source) ? null : Boolean.parseBoolean(source);
	}

	@Override
	public String to(Boolean source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return source == null ? null : Boolean.toString(source);
	}
}
