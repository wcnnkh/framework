package io.basc.framework.transform.factory.config;

import io.basc.framework.register.Registration;
import io.basc.framework.transform.Transformer;
import io.basc.framework.transform.factory.TransformerFactory;

public interface TransformerRegistry<S, E extends Throwable> extends TransformerFactory<S, E> {
	<T> Registration registerTransformer(Class<T> targetType,
			Transformer<? super S, ? extends T, ? extends E> transformer);
}
