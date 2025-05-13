package run.soeasy.framework.core.convert.registry;

import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.exchange.Registration;

public interface ConverterRegistry<S, E extends Throwable> extends ConverterFactory<S, E> {
	<T> Registration registerConverter(Class<T> targetType, Converter<? super S, ? extends T, ? extends E> converter);
}
