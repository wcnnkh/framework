package run.soeasy.framework.core.convert.transform;

import lombok.NonNull;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.spi.ServiceMap;

public class DefaultTransformerRegistry<S, E extends Throwable>
		extends ServiceMap<Transformer<? super S, ?, ? extends E>> implements TransformerRegistry<S, E> {

	@SuppressWarnings("unchecked")
	@Override
	public <T> Transformer<S, T, E> getTransformer(@NonNull Class<? extends T> requiredType) {
		return (Transformer<S, T, E>) search(requiredType).first();
	}

	@Override
	public <T> Registration registerTransformer(@NonNull Class<T> requiredType,
			@NonNull Transformer<? super S, ? super T, ? extends E> transformer) {
		return register(requiredType, transformer);
	}
}
