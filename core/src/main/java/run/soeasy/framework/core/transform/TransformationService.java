package run.soeasy.framework.core.transform;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.spi.ServiceInjectors;

@Getter
public class TransformationService implements Transformer {
	private final ServiceInjectors<Transformer> injectors = new ServiceInjectors<>();
	private final Transformers transformers = new Transformers();
	private final TransformerRegistry registry = new TransformerRegistry();

	public TransformationService() {
		injectors.register((service) -> {
			if (service instanceof TransformationService) {
				TransformerAware transformerAware = (TransformerAware) service;
				transformerAware.setTransformer(this);
			}
			return Registration.SUCCESS;
		});
	}

	public Registration register(Transformer transformer) {
		Registration registration = transformers instanceof ConditionalTransformer
				? registry.register((ConditionalTransformer) transformer)
				: transformers.register(transformer);
		if (!registration.isCancelled()) {
			injectors.inject(transformer);
		}
		return registration;
	}

	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return registry.canTransform(sourceTypeDescriptor, targetTypeDescriptor)
				|| transformers.canTransform(sourceTypeDescriptor, targetTypeDescriptor);
	}

	@Override
	public boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull Object target, @NonNull TypeDescriptor targetTypeDescriptor) {
		if (registry.canTransform(sourceTypeDescriptor, targetTypeDescriptor)) {
			return registry.transform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
		}
		return transformers.transform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
	}
}
