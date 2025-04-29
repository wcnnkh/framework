package run.soeasy.framework.core.convert.transform;

import lombok.NonNull;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.spi.ServiceMap;

public class DefaultReversibleTransformerRegistry<S, E extends Throwable> extends
		ServiceMap<ReversibleTransformer<? super S, ?, ? extends E>> implements ReversibleTransformerRegistry<S, E> {
	private final DefaultTransformerRegistry<S, E> transformerRegistry = new DefaultTransformerRegistry<>();

	@SuppressWarnings("unchecked")
	@Override
	public <T> ReversibleTransformer<S, T, E> getReversibleTransformer(@NonNull Class<? extends T> requiredType) {
		return (ReversibleTransformer<S, T, E>) search(requiredType).first();
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
