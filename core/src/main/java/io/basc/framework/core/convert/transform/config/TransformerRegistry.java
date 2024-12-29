package io.basc.framework.core.convert.transform.config;

import io.basc.framework.core.convert.transform.Transformer;
import io.basc.framework.util.Registration;
import lombok.NonNull;

public interface TransformerRegistry<S, E extends Throwable> extends TransformerFactory<S, E> {
	<T> Registration registerTransformer(@NonNull Class<T> requiredType,
			@NonNull Transformer<? super S, ? super T, ? extends E> transformer);
}
