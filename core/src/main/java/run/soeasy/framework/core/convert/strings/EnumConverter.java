package run.soeasy.framework.core.convert.strings;

import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ReversibleConverter;
import run.soeasy.framework.core.convert.TypeDescriptor;

@SuppressWarnings("rawtypes")
public class EnumConverter implements ReversibleConverter<String, Enum, ConversionException> {

	@SuppressWarnings({ "unchecked" })
	@Override
	public Enum<?> convert(String source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return StringUtils.isEmpty(source) ? null : Enum.valueOf((Class<? extends Enum>) targetType.getType(), source);
	}

	@Override
	public String reverseConvert(Enum source, TypeDescriptor sourceType, TypeDescriptor targetType) throws ConversionException {
		return source == null ? null : source.name();
	}

}
