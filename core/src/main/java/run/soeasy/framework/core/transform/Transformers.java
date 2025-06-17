package run.soeasy.framework.core.transform;

import java.util.Comparator;
import java.util.Set;

import lombok.NonNull;
import run.soeasy.framework.core.comparator.ComparableComparator;
import run.soeasy.framework.core.convert.ConditionalConverter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.TypeMapping;
import run.soeasy.framework.core.spi.ConfigurableServices;

public class Transformers extends ConfigurableServices<Transformer> implements Transformer, Comparator<Transformer> {
	public Transformers() {
		setComparator(this);
	}

	@Override
	public int compare(Transformer o1, Transformer o2) {
		if (o1 instanceof ConditionalTransformer && o2 instanceof ConditionalTransformer) {
			Set<TypeMapping> pairs = ((ConditionalTransformer) o1).getTransformableTypeMappings();
			Set<TypeMapping> otherPairs = ((ConditionalTransformer) o2).getTransformableTypeMappings();
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
	public boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return anyMatch((e) -> e.canTransform(sourceTypeDescriptor, targetTypeDescriptor));
	}

	@Override
	public boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull Object target, @NonNull TypeDescriptor targetTypeDescriptor) {
		for (Transformer transformer : this) {
			if (transformer.canTransform(sourceTypeDescriptor, targetTypeDescriptor)) {
				return transformer.transform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
			}
		}
		return false;
	}
}
