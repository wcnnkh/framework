package run.soeasy.framework.core.convert;

import run.soeasy.framework.util.exchange.Registration;
import run.soeasy.framework.util.spi.ServiceMap;

public class DefaultConverterRegistry<S, E extends Throwable> extends ServiceMap<Converter<? super S, ?, ? extends E>>
		implements ConverterRegistry<S, E> {

	@SuppressWarnings("unchecked")
	@Override
	public <T> Converter<S, T, E> getConverter(Class<? extends T> requiredType) {
		return (Converter<S, T, E>) search(requiredType).first();
	}

	@Override
	public <T> Registration registerConverter(Class<T> requiredType,
			Converter<? super S, ? extends T, ? extends E> converter) {
		remove(requiredType);
		return register(requiredType, converter);
	}
}
