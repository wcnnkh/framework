package io.basc.framework.transform.factory.config;

import io.basc.framework.convert.transform.Transformer;
import io.basc.framework.transform.factory.TransformerFactory;
import io.basc.framework.util.register.Registration;

public interface TransformerRegistry<S, E extends Throwable> extends TransformerFactory<S, E> {
	<T> Registration registerTransformer(Class<T> targetType,
			Transformer<? super S, ? extends T, ? extends E> transformer);
}
