package run.soeasy.framework.core.transform;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.container.map.TreeMapContainer;
import run.soeasy.framework.core.spi.ServiceInjectors;
import run.soeasy.framework.core.type.TypeMapping;

@Getter
public class TransformerRegistry implements ConditionalTransformationService {
	private final ServiceInjectors<Transformer<?, ?>> injectors = new ServiceInjectors<>();
	private final Transformers transformers = new Transformers();
	private final TreeMapContainer<TypeMapping, CustomizeConditionalTransformationService> registry = new TreeMapContainer<>();

	public TransformerRegistry() {
		transformers.getInjectors().add(injectors);
		injectors.register((service) -> {
			if (service instanceof TransformationService) {
				TransformationServiceAware transformationServiceAware = (TransformationServiceAware) service;
				transformationServiceAware.setTransformationService(this);
			}
			return Registration.SUCCESS;
		});
	}

	public Registration registerReversibleTransformer(@NonNull TypeMapping typeMapping,
			@NonNull ReversibleTransformer<?, ?> reversibleTransformer) {
		Registration registration = registry.register(typeMapping,
				new CustomizeConditionalTransformationService(typeMapping, reversibleTransformer));
		if (!registration.isCancelled()) {
			injectors.inject(reversibleTransformer);
		}
		return registration;
	}

	public final <S, T> Registration registerReversibleTransformer(@NonNull Class<S> sourceClass,
			@NonNull Class<T> targetClass, @NonNull ReversibleTransformer<? super S, ? super T> reversibleTransformer) {
		return registerReversibleTransformer(new TypeMapping(sourceClass, targetClass), reversibleTransformer);
	}

	public final <S, T> Registration registerTransformer(@NonNull Class<S> sourceClass, @NonNull Class<T> targetClass,
			Transformer<? super S, ? super T> transformer, Transformer<? super T, ? super S> reversedTransformer) {
		Registration registration = registerReversibleTransformer(sourceClass, targetClass,
				new ReversedTransformer<>(transformer, reversedTransformer));
		if (!registration.isCancelled()) {
			if (transformer != null) {
				injectors.inject(transformer);
			}

			if (reversedTransformer != null) {
				injectors.inject(reversedTransformer);
			}
		}
		return registration;
	}

	private TransformationService getTransformationService(TypeDescriptor sourceTypeDescriptor,
			TypeDescriptor targetTypeDescriptor) {
		return null;
	}

	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return getTransformationService(sourceTypeDescriptor, targetTypeDescriptor) != null
				|| transformers.canTransform(sourceTypeDescriptor, targetTypeDescriptor);
	}

	@Override
	public boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull Object target, @NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		TransformationService transformationService = getTransformationService(sourceTypeDescriptor,
				targetTypeDescriptor);
		if (transformationService != null) {
			return transformationService.transform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
		}
		return transformers.transform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
	}

	@Override
	public Set<TypeMapping> getTransformableTypeMappings() {
		return Stream.concat(registry.values().stream().flatMap((e) -> e.getTransformableTypeMappings().stream()),
				transformers.getTransformableTypeMappings().stream()).collect(Collectors.toSet());
	}

}
