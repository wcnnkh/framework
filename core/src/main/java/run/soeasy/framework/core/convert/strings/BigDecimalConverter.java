package run.soeasy.framework.core.convert.strings;

import java.math.BigDecimal;

import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ReversibleConverter;
import run.soeasy.framework.core.convert.TypeDescriptor;

public class BigDecimalConverter implements ReversibleConverter<String, BigDecimal> {

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
