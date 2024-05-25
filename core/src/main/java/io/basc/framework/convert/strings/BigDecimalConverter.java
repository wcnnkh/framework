package io.basc.framework.convert.strings;

import java.math.BigDecimal;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ReversibleConverter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.StringUtils;

public class BigDecimalConverter implements ReversibleConverter<String, BigDecimal, ConversionException> {

	@Override
	public BigDecimal convert(String source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return StringUtils.isEmpty(source) ? null : new BigDecimal(source);
	}

	@Override
	public String reverseConvert(BigDecimal source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return source == null ? null : source.toString();
	}

}
