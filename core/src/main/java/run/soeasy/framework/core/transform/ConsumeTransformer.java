package run.soeasy.framework.core.transform;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.TypeMapping;

class ConsumeTransformer<S, T> implements ConditionalTransformer {
	private final TypeMapping typeMapping;
	private final BiConsumer<? super S, ? super T> consumer;

	public ConsumeTransformer(@NonNull Class<S> sourceType, @NonNull Class<T> targetType,
			@NonNull BiConsumer<? super S, ? super T> consumer) {
		this.typeMapping = new TypeMapping(sourceType, targetType);
		this.consumer = consumer;
	}

	@Override
	public Set<TypeMapping> getTransformableTypeMappings() {
		return Collections.singleton(typeMapping);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull Object target, @NonNull TypeDescriptor targetTypeDescriptor) {
		consumer.accept((S) source, (T) target);
		return true;
	}
}
