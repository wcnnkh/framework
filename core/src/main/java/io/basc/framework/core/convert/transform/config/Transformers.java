package io.basc.framework.core.convert.transform.config;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.Transformer;
import io.basc.framework.util.spi.ConfigurableServices;
import lombok.NonNull;

public class Transformers<S, T, E extends Throwable, R extends Transformer<? super S, ? super T, ? extends E>>
		extends ConfigurableServices<R> implements Transformer<S, T, E> {
	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType) {
		return anyMatch((e) -> e.canTransform(sourceType, targetType));
	}

	@Override
	public void transform(@NonNull S source, @NonNull TypeDescriptor sourceType, @NonNull T target,
			@NonNull TypeDescriptor targetType) throws E {
		for (R transform : this) {
			if (transform.canTransform(sourceType, targetType)) {
				transform.transform(source, sourceType, target, targetType);
				return;
			}
		}
	}
}
