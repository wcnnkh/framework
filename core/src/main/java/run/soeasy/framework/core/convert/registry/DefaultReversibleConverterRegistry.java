package run.soeasy.framework.core.convert.registry;

import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.ReversibleConverter;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.spi.ServiceMap;

public class DefaultReversibleConverterRegistry<S, E extends Throwable> extends
		ServiceMap<ReversibleConverter<? super S, ?, ? extends E>> implements ReversibleConverterRegistry<S, E> {
	private final DefaultConverterRegistry<S, E> converterRegistry = new DefaultConverterRegistry<>();

	public DefaultConverterRegistry<S, E> getConverterRegistry() {
		return converterRegistry;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> ReversibleConverter<S, T, E> getReversibleConverter(Class<? extends T> requiredType) {
		return (ReversibleConverter<S, T, E>) search(requiredType).first();
	}

	@Override
	public <T> Converter<S, T, E> getConverter(Class<? extends T> requiredType) {
		Converter<S, T, E> converter = converterRegistry.getConverter(requiredType);
		if (converter == null) {
			converter = getReversibleConverter(requiredType);
		}
		return converter;
	}

	@Override
	public <T> Registration registerReversibleConverter(Class<T> requiredType,
			ReversibleConverter<S, T, ? extends E> converter) {
		remove(requiredType);
		return register(requiredType, converter);
	}
}
