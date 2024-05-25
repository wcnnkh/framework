package io.basc.framework.convert.strings;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ReversibleConverter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.StringUtils;
import lombok.Data;

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
