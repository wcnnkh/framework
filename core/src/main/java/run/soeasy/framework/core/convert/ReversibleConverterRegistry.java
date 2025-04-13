package run.soeasy.framework.core.convert;

import run.soeasy.framework.core.exchange.Registration;

public interface ReversibleConverterRegistry<S, E extends Throwable> extends ReversibleConverterFactory<S, E> {
	<T> Registration registerReversibleConverter(Class<T> targetType, ReversibleConverter<S, T, ? extends E> converter);
}
