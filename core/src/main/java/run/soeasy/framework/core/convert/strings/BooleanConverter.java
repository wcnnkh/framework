package run.soeasy.framework.core.convert.strings;

import java.util.HashSet;

import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ReversibleConverter;
import run.soeasy.framework.core.convert.TypeDescriptor;

public class BooleanConverter extends HashSet<String> implements ReversibleConverter<String, Boolean> {
	private static final long serialVersionUID = 1L;

	public Boolean convert(String source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		Boolean value = StringUtils.isEmpty(source) ? null : Boolean.parseBoolean(source);
		if (value == null && targetType.isPrimitive()) {
			return false;
		}
		return value;
	}

	@Override
	public String reverseConvert(Boolean source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return source == null ? null : Boolean.toString(source);
	}
}
