package run.soeasy.framework.core.convert.strings;

import lombok.Getter;
import lombok.Setter;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;

@Getter
@Setter
public class StringToLongConverter implements StringConverter<Long> {
	public static StringToLongConverter DEFAULT = new StringToLongConverter();

	private int radix = 10;
	private boolean unsigned = false;

	@Override
	public Long from(String source, TypeDescriptor sourceType, TypeDescriptor targetType) throws ConversionException {
		if (StringUtils.isEmpty(source)) {
			return null;
		}
		return unsigned ? Long.parseUnsignedLong(source, radix) : Long.parseLong(source, radix);
	}

	@Override
	public String to(Long source, TypeDescriptor sourceType, TypeDescriptor targetType) throws ConversionException {
		if (source == null) {
			return null;
		}

		return unsigned ? Long.toUnsignedString(source, radix) : Long.toString(source, radix);
	}

}
