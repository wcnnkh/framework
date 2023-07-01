package io.basc.framework.convert.support;

import java.util.Map.Entry;
import java.util.TreeMap;

import io.basc.framework.convert.Converter;
import io.basc.framework.convert.ConverterRegistry;
import io.basc.framework.util.Assert;
import io.basc.framework.util.comparator.TypeComparator;

public class DefaultConverterRegistry<S, E extends Throwable> implements ConverterRegistry<S, E> {
	private TreeMap<Class<?>, Converter<? super S, ?, ? extends E>> converterMap;

	protected <T> T get(Class<?> type, TreeMap<Class<?>, T> sourceMap) {
		if (sourceMap == null || sourceMap.isEmpty()) {
			return null;
		}

		Entry<Class<?>, T> entry = sourceMap.ceilingEntry(type);
		return entry == null ? null : entry.getValue();
	}

	protected <T> TreeMap<Class<?>, T> register(Class<?> type, T conversion, TreeMap<Class<?>, T> sourceMap) {
		Assert.requiredArgument(type != null, "type");
		if (conversion == null) {
			if (sourceMap != null) {
				sourceMap.remove(type);
			}
		} else {
			if (sourceMap == null) {
				sourceMap = new TreeMap<>(TypeComparator.DEFAULT);
			}

			sourceMap.put(type, conversion);
		}
		return sourceMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Converter<S, T, E> getConverter(Class<? extends T> type) {
		return (Converter<S, T, E>) get(type, converterMap);
	}

	@Override
	public <T> void registerConverter(Class<T> type, Converter<? super S, ? extends T, ? extends E> converter) {
		this.converterMap = register(type, converter, converterMap);
	}

}
