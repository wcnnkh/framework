package io.basc.framework.convert.config.support;

import java.util.TreeMap;

import io.basc.framework.convert.Converter;
import io.basc.framework.convert.Inverter;
import io.basc.framework.convert.ReversibleConverter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.config.ReversibleConverterRegistry;

public class DefaultReversibleConverterRegistry<S, E extends Throwable> extends DefaultConverterRegistry<S, E>
		implements ReversibleConverterRegistry<S, E> {
	private TreeMap<Class<?>, Inverter<? extends S, ?, ? extends E>> inverterMap;
	private TreeMap<Class<?>, ReversibleConverter<S, ?, ? extends E>> reversibleConverterMap;

	@SuppressWarnings("unchecked")
	@Override
	public <T> Inverter<S, T, E> getInverter(Class<? extends T> type) {
		Inverter<S, T, E> inverter = (Inverter<S, T, E>) get(type, inverterMap);
		if (inverter == null) {
			inverter = getReversibleConverter(type);
		}
		return inverter;
	}

	@Override
	public <T> Converter<S, T, E> getConverter(Class<? extends T> type) {
		Converter<S, T, E> converter = super.getConverter(type);
		if (converter == null) {
			converter = getReversibleConverter(type);
		}
		return converter;
	}

	@Override
	public <T> void registerInverter(Class<T> type, Inverter<? extends S, ? super T, ? extends E> inverter) {
		this.inverterMap = register(type, inverter, inverterMap);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> ReversibleConverter<S, T, E> getReversibleConverter(Class<? extends T> type) {
		return (ReversibleConverter<S, T, E>) get(type, reversibleConverterMap);
	}

	@Override
	public <T> void registerReversibleConverter(Class<T> type,
			ReversibleConverter<S, T, ? extends E> reversibleConverter) {
		this.reversibleConverterMap = register(type, reversibleConverter, reversibleConverterMap);
	}

	@Override
	public Object convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		if (canDirectlyConvert(sourceType, targetType)) {
			return source;
		}
		return super.convert(source, sourceType, targetType);
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return canDirectlyConvert(sourceType, targetType) || super.canConvert(sourceType, targetType);
	}
}
