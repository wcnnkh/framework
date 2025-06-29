package run.soeasy.framework.core.convert.strings;

import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;

@SuppressWarnings("rawtypes")
public class StringToEnumConverter implements StringConverter<Enum> {
	public static StringToEnumConverter DEFAULT = new StringToEnumConverter();

	@SuppressWarnings({ "unchecked" })
	@Override
	public Enum<?> from(String source, TypeDescriptor sourceType, TypeDescriptor targetType) throws ConversionException {
		return StringUtils.isEmpty(source) ? null : Enum.valueOf((Class<? extends Enum>) targetType.getType(), source);
	}

	@Override
	public String to(Enum source, TypeDescriptor sourceType, TypeDescriptor targetType) throws ConversionException {
		return source == null ? null : source.name();
	}

}
