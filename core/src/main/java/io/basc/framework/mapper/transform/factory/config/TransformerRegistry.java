package io.basc.framework.mapper.transform.factory.config;

import io.basc.framework.mapper.transform.Transformer;
import io.basc.framework.mapper.transform.factory.TransformerFactory;
import io.basc.framework.util.Registration;

public interface TransformerRegistry<S, E extends Throwable, C extends Transformer<? super S, ? super Object, ? extends E>>
		extends TransformerFactory<S, E> {
	Registration registerTransformer(Class<?> requiredType, C transformer);

	boolean isTransformerRegistred(Class<?> requiredType);
}
