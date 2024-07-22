package io.basc.framework.transform.factory.config;

import io.basc.framework.register.Registration;
import io.basc.framework.transform.ReversibleTransformer;
import io.basc.framework.transform.factory.ReversibleTransformerFactory;

public interface ReversibleTransformerRegistry<S, E extends Throwable> extends ReversibleTransformerFactory<S, E> {
	<T> Registration registerReversibleTransformer(Class<T> targetType,
			ReversibleTransformer<S, T, ? extends E> reversibleTransformer);
}
