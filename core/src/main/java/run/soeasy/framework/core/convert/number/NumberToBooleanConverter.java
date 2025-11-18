package run.soeasy.framework.core.convert.number;

import run.soeasy.framework.core.NumberUtils;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ReversibleConverter;
import run.soeasy.framework.core.convert.TypeDescriptor;

public class NumberToBooleanConverter implements ReversibleConverter<Number, Boolean> {
	public static final NumberToBooleanConverter INSTANCE = new NumberToBooleanConverter();
	
	@Override
	public Boolean to(Number source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		return NumberUtils.toBoolean(source);
	}

	@Override
	public Number from(Boolean source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		if (source == null) {
			return null;
		}
		return source ? 1 : 0;
	}

}
