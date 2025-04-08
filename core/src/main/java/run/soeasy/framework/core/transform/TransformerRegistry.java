package run.soeasy.framework.core.transform;

import lombok.NonNull;
import run.soeasy.framework.util.exchange.Registration;

public interface TransformerRegistry<S, E extends Throwable> extends TransformerFactory<S, E> {
	<T> Registration registerTransformer(@NonNull Class<T> requiredType,
			@NonNull Transformer<? super S, ? super T, ? extends E> transformer);
}
