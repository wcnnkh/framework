package run.soeasy.framework.core.convert.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.spi.ConfigurableServices;

public class Transformers<S, T, E extends Throwable, R extends Transformer<? super S, ? super T, ? extends E>>
		extends ConfigurableServices<R> implements Transformer<S, T, E> {
	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType) {
		return anyMatch((e) -> e.canTransform(sourceType, targetType));
	}

	@Override
	public boolean transform(@NonNull S source, @NonNull TypeDescriptor sourceType, @NonNull T target,
			@NonNull TypeDescriptor targetType) throws E {
		for (R transform : this) {
			if (transform.canTransform(sourceType, targetType)) {
				return transform.transform(source, sourceType, target, targetType);
			}
		}
		return false;
	}
}
