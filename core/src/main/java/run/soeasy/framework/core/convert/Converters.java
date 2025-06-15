package run.soeasy.framework.core.convert;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.NonNull;
import run.soeasy.framework.core.comparator.ComparableComparator;
import run.soeasy.framework.core.spi.ConfigurableServices;
import run.soeasy.framework.core.type.TypeMapping;

public class Converters extends ConfigurableServices<Converter<? super Object, ? extends Object>>
		implements ConditionalConverter<Object, Object>, Comparator<Converter<? super Object, ? extends Object>> {
	public Converters() {
		setComparator(this);
	}

	@Override
	public int compare(Converter<? super Object, ? extends Object> o1, Converter<? super Object, ? extends Object> o2) {
		if (o1 instanceof ConditionalConverter && o2 instanceof ConditionalConverter) {
			Set<TypeMapping> pairs = ((ConditionalConverter<?, ?>) o1).getConvertibleTypeMappings();
			Set<TypeMapping> otherPairs = ((ConditionalConverter<?, ?>) o2).getConvertibleTypeMappings();
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
	public Set<TypeMapping> getConvertibleTypeMappings() {
		return stream().filter((e) -> e instanceof ConditionalConverter).map((e) -> (ConditionalConverter<?, ?>) e)
				.flatMap((e) -> e.getConvertibleTypeMappings().stream()).collect(Collectors.toSet());
	}

	@Override
	public Object convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		for (Converter<? super Object, ? extends Object> conversionService : this) {
			if (conversionService.canConvert(sourceTypeDescriptor, targetTypeDescriptor)) {
				return conversionService.convert(source, sourceTypeDescriptor, targetTypeDescriptor);
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
