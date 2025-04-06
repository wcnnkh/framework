package run.soeasy.framework.core.transform.config;

import run.soeasy.framework.core.transform.ReversibleTransformer;
import run.soeasy.framework.util.exchange.Registration;

public interface ReversibleTransformerRegistry<S, E extends Throwable> extends ReversibleTransformerFactory<S, E> {
	<T> Registration registerReversibleTransformer(Class<T> requiredType,
			ReversibleTransformer<S, T, ? extends E> reversibleTransformer);
}
