package run.soeasy.framework.core.convert;

import lombok.NonNull;
import run.soeasy.framework.core.spi.ConfigurableServices;

public class Converters extends ConfigurableServices<Converter> implements Converter {

	public Converters() {
		setComparator(ConverterComparator.DEFAULT);
	}

	@Override
	public Object convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		for (Converter converter : this) {
			if (converter.canConvert(sourceTypeDescriptor, targetTypeDescriptor)) {
				return converter.convert(source, sourceTypeDescriptor, targetTypeDescriptor);
			}
		}
		throw new ConverterNotFoundException(sourceTypeDescriptor, targetTypeDescriptor);
	}

	@Override
	public boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return anyMatch((e) -> e.canConvert(sourceTypeDescriptor, targetTypeDescriptor));
	}
}
