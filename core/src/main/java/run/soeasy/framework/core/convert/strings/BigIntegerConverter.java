package run.soeasy.framework.core.convert.strings;

import java.math.BigInteger;

import lombok.Data;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ReversibleConverter;
import run.soeasy.framework.core.convert.TypeDescriptor;

@Data
public class BigIntegerConverter implements ReversibleConverter<String, BigInteger> {
	private int radix = 10;

	@Override
	public BigInteger convert(String source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return StringUtils.isEmpty(source) ? null : new BigInteger(source, radix);
	}

	@Override
	public String reverseConvert(BigInteger source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return source == null ? null : source.toString(radix);
	}

}
