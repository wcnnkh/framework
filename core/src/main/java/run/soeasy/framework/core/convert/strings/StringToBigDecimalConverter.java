package run.soeasy.framework.core.convert.strings;

import java.math.BigDecimal;

import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;

public class StringToBigDecimalConverter implements StringConverter<BigDecimal> {
	public static StringToBigDecimalConverter DEFAULT = new StringToBigDecimalConverter();

	@Override
	public BigDecimal from(String source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return StringUtils.isEmpty(source) ? null : new BigDecimal(source);
	}

	@Override
	public String to(BigDecimal source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		return source == null ? null : source.toString();
	}
}
