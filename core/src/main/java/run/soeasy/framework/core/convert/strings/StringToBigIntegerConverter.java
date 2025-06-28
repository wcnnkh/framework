package run.soeasy.framework.core.convert.strings;

import java.math.BigInteger;

import lombok.Getter;
import lombok.Setter;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;

@Getter
@Setter
public class StringToBigIntegerConverter implements StringConverter<BigInteger> {
	public static StringToBigIntegerConverter DEFAULT = new StringToBigIntegerConverter();

	private int radix = 10;

	@Override
	public BigInteger from(String source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return StringUtils.isEmpty(source) ? null : new BigInteger(source, radix);
	}

	@Override
	public String to(BigInteger source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return source == null ? null : source.toString(radix);
	}

}
