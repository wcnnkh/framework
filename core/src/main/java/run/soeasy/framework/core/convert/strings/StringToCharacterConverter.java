package run.soeasy.framework.core.convert.strings;

import lombok.Getter;
import lombok.Setter;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;

@Getter
@Setter
public class StringToCharacterConverter implements StringConverter<Character> {
	public static StringToCharacterConverter DEFAULT = new StringToCharacterConverter();

	private int index = 0;

	@Override
	public Character from(String source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (StringUtils.isEmpty(source)) {
			return null;
		}

		return source.charAt(index);
	}

	@Override
	public String to(Character source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return source == null ? null : source.toString();
	}

}
