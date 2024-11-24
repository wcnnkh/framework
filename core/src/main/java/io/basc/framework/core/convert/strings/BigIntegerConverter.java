package io.basc.framework.core.convert.strings;

import java.math.BigInteger;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.ReversibleConverter;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.util.StringUtils;
import lombok.Data;

@Data
public class BigIntegerConverter implements ReversibleConverter<String, BigInteger, ConversionException> {
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
