package run.soeasy.framework.core.convert.support;

import lombok.NonNull;
import run.soeasy.framework.core.comparator.Ordered;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;

class ObjectToStringConverter implements Converter, Ordered {
	public static final ObjectToStringConverter DEFAULT = new ObjectToStringConverter();

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}
	
	@Override
	public boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return targetTypeDescriptor.getType() == String.class;
	}

	@Override
	public Object convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		return source == null ? null : source.toString();
	}
}
