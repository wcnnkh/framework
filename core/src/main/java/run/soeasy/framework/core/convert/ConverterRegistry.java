package run.soeasy.framework.core.convert;

import run.soeasy.framework.util.exchange.Registration;

public interface ConverterRegistry<S, E extends Throwable> extends ConverterFactory<S, E> {
	<T> Registration registerConverter(Class<T> targetType, Converter<? super S, ? extends T, ? extends E> converter);
}
