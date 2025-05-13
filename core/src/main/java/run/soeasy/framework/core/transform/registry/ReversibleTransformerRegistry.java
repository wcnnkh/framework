package run.soeasy.framework.core.transform.registry;

import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.transform.ReversibleTransformer;

public interface ReversibleTransformerRegistry<S, E extends Throwable> extends ReversibleTransformerFactory<S, E> {
	<T> Registration registerReversibleTransformer(Class<T> requiredType,
			ReversibleTransformer<S, T, ? extends E> reversibleTransformer);
}
