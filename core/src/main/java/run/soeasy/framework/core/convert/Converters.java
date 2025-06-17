package run.soeasy.framework.core.convert;

import java.util.Comparator;
import java.util.Set;

import lombok.NonNull;
import run.soeasy.framework.core.comparator.ComparableComparator;
import run.soeasy.framework.core.spi.ConfigurableServices;

public class Converters extends ConfigurableServices<Converter> implements Converter, Comparator<Converter> {
	public Converters() {
		setComparator(this);
	}

	@Override
	public int compare(Converter o1, Converter o2) {
		if (o1 instanceof ConditionalConverter && o2 instanceof ConditionalConverter) {
			Set<TypeMapping> pairs = ((ConditionalConverter) o1).getConvertibleTypeMappings();
			Set<TypeMapping> otherPairs = ((ConditionalConverter) o2).getConvertibleTypeMappings();
			for (TypeMapping pair : pairs) {
				for (TypeMapping other : otherPairs) {
					if (pair.compareTo(other) == -1) {
						return -1;
					}
				}
			}

			return ComparableComparator.INSTANCE.compare(o1, o2) == 1 ? 1 : -1;
		}

		if (o1 instanceof ConditionalConverter) {
			return -1;
		}

		if (o2 instanceof ConditionalConverter) {
			return 1;
		}

		return ComparableComparator.INSTANCE.compare(o1, o2) == 1 ? 1 : -1;
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
