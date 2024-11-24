package io.basc.framework.transform.factory.config;

import io.basc.framework.core.convert.transform.ReversibleTransformer;
import io.basc.framework.transform.factory.ReversibleTransformerFactory;
import io.basc.framework.util.register.Registration;

public interface ReversibleTransformerRegistry<S, E extends Throwable> extends ReversibleTransformerFactory<S, E> {
	<T> Registration registerReversibleTransformer(Class<T> targetType,
			ReversibleTransformer<S, T, ? extends E> reversibleTransformer);
}
