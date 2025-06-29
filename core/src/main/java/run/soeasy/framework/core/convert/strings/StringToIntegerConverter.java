package run.soeasy.framework.core.convert.strings;

import lombok.Getter;
import lombok.Setter;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;

@Getter
@Setter
public class StringToIntegerConverter implements StringConverter<Integer> {
	public static StringToIntegerConverter DEFAULT = new StringToIntegerConverter();
	private int radix = 10;
	private boolean unsigned = false;

	@Override
	public Integer from(String source, TypeDescriptor sourceType, TypeDescriptor targetType) throws ConversionException {
		if (StringUtils.isEmpty(source)) {
			return null;
		}

		return unsigned ? Integer.parseUnsignedInt(source, radix) : Integer.valueOf(source, radix);
	}

	@Override
	public String to(Integer source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (source == null) {
			return null;
		}

		return unsigned ? Integer.toUnsignedString(source, radix) : Integer.toString(source, radix);
	}

}
