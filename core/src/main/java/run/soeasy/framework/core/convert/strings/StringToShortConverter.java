package run.soeasy.framework.core.convert.strings;

import lombok.Getter;
import lombok.Setter;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;

@Getter
@Setter
public class StringToShortConverter implements StringConverter<Short> {
	public static StringToShortConverter DEFAULT = new StringToShortConverter();
	
	private int radix = 10;

	@Override
	public Short from(String source, TypeDescriptor sourceType, TypeDescriptor targetType) throws ConversionException {
		if (StringUtils.isEmpty(source)) {
			return targetType.isPrimitive() ? (short) 0 : null;
		}
		return Short.parseShort(source, radix);
	}

	@Override
	public String to(Short source, TypeDescriptor sourceType, TypeDescriptor targetType) throws ConversionException {
		return source == null ? null : Short.toString(source);
	}

}
