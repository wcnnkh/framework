package run.soeasy.framework.core.transform;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConverterNotFoundException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.TypeMapping;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.Registrations;
import run.soeasy.framework.core.exchange.container.map.TreeMapContainer;

public class TransformerRegistry extends TreeMapContainer<TypeMapping, Transformer> implements ConditionalTransformer {

	private Transformer getTransformer(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		Transformer transformer = getTransformerByHash(sourceTypeDescriptor, targetTypeDescriptor);
		if (transformer == null) {
			transformer = getTransformerByHash(targetTypeDescriptor, sourceTypeDescriptor);
		}

		for (Entry<TypeMapping, Transformer> entry : entrySet()) {
			if (entry.getValue().canTransform(sourceTypeDescriptor, targetTypeDescriptor)) {
				return entry.getValue();
			}
		}
		return null;
	}

	private Transformer getTransformerByHash(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		TypeMapping typeMapping = new TypeMapping(sourceTypeDescriptor.getType(), targetTypeDescriptor.getType());
		return get(typeMapping);
	}

	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return getTransformer(sourceTypeDescriptor, targetTypeDescriptor) != null;
	}

	@Override
	public boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull Object target, @NonNull TypeDescriptor targetTypeDescriptor) {
		Transformer transformer = getTransformer(sourceTypeDescriptor, targetTypeDescriptor);
		if (transformer == null) {
			throw new ConverterNotFoundException(sourceTypeDescriptor, targetTypeDescriptor);
		}
		return transformer.transform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
	}

	@Override
	public Set<TypeMapping> getTransformableTypeMappings() {
		return keySet();
	}

	public Registration register(ConditionalTransformer conditionalTransformer) {
		Set<TypeMapping> typeMappings = conditionalTransformer.getTransformableTypeMappings();
		List<Registration> registrations = typeMappings.stream().map((e) -> register(e, conditionalTransformer))
				.collect(Collectors.toList());
		return Registrations.forList(registrations);
	}

	public <S, T> Registration register(Class<S> sourceType, Class<T> targetType,
			BiConsumer<? super S, ? super T> consumer) {
		ConsumeTransformer<S, T> transformer = new ConsumeTransformer<>(sourceType, targetType, consumer);
		return register(transformer);
	}
}
