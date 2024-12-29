package io.basc.framework.core.convert.transform.config;

import io.basc.framework.core.convert.transform.ReversibleTransformer;
import io.basc.framework.util.Registration;

public interface ReversibleTransformerRegistry<S, E extends Throwable> extends ReversibleTransformerFactory<S, E> {
	<T> Registration registerReversibleTransformer(Class<T> requiredType,
			ReversibleTransformer<S, T, ? extends E> reversibleTransformer);
}
