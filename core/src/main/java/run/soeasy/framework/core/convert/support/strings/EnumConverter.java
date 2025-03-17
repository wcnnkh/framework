package run.soeasy.framework.core.convert.support.strings;

import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ReversibleConverter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.util.StringUtils;

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
