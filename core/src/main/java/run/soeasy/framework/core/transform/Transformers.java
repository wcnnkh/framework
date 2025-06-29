package run.soeasy.framework.core.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.spi.ConfigurableServices;

public class Transformers extends ConfigurableServices<Transformer> implements Transformer {
	public Transformers() {
		setComparator(TransformerComparator.DEFAULT);
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
