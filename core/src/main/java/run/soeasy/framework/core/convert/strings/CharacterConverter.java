package run.soeasy.framework.core.convert.strings;

import lombok.Data;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ReversibleConverter;
import run.soeasy.framework.core.convert.TypeDescriptor;

@Data
public class CharacterConverter implements ReversibleConverter<String, Character, ConversionException> {
	private int index = 0;
	private char defaultValue = (char) 0;

	@Override
	public Character convert(String source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (StringUtils.isEmpty(source)) {
			return targetType.isPrimitive() ? defaultValue : null;
		}

		return source.charAt(index);
	}

	@Override
	public String reverseConvert(Character source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return source == null ? null : source.toString();
	}

}
