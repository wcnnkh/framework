package run.soeasy.framework.core.convert.config;

import run.soeasy.framework.core.convert.ReversibleConverter;
import run.soeasy.framework.util.exchange.Registration;

public interface ReversibleConverterRegistry<S, E extends Throwable> extends ReversibleConverterFactory<S, E> {
	<T> Registration registerReversibleConverter(Class<T> targetType, ReversibleConverter<S, T, ? extends E> converter);
}
