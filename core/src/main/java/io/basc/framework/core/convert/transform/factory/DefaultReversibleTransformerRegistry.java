package io.basc.framework.core.convert.transform.factory;

import io.basc.framework.core.convert.transform.ReversibleTransformer;
import io.basc.framework.core.convert.transform.Transformer;
import io.basc.framework.util.Registration;
import io.basc.framework.util.spi.ServiceMap;
import lombok.NonNull;

public class DefaultReversibleTransformerRegistry<S, E extends Throwable> extends
		ServiceMap<ReversibleTransformer<? super S, ?, ? extends E>> implements ReversibleTransformerRegistry<S, E> {
	private final DefaultTransformerRegistry<S, E> transformerRegistry = new DefaultTransformerRegistry<>();

	@SuppressWarnings("unchecked")
	@Override
	public <T> ReversibleTransformer<S, T, E> getReversibleTransformer(@NonNull Class<? extends T> requiredType) {
		return (ReversibleTransformer<S, T, E>) getFirst(requiredType);
	}

	@Override
	public <T> Transformer<S, T, E> getTransformer(@NonNull Class<? extends T> requiredType) {
		Transformer<S, T, E> transformer = transformerRegistry.getTransformer(requiredType);
		if (transformer == null) {
			transformer = ReversibleTransformerRegistry.super.getTransformer(requiredType);
		}
		return transformer;
	}

	@Override
	public <T> Registration registerReversibleTransformer(Class<T> requiredType,
			ReversibleTransformer<S, T, ? extends E> reversibleTransformer) {
		return register(requiredType, reversibleTransformer);
	}

}
