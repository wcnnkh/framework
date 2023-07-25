package io.basc.framework.convert.config.support;

import java.util.Map.Entry;
import java.util.TreeMap;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.Converter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.config.ConversionComparator;
import io.basc.framework.convert.config.ConverterRegistry;
import io.basc.framework.util.Assert;
import io.basc.framework.util.check.NestingChecker;
import io.basc.framework.util.check.ThreadLocalNestingChecker;
import io.basc.framework.util.comparator.TypeComparator;
import io.basc.framework.util.registry.Registration;

public class DefaultConverterRegistry<S, E extends Throwable> extends ConfigurableServices<ConversionService>
		implements ConverterRegistry<S, E> {
	private static final NestingChecker<ConversionService> NESTING_CHECKERS = new ThreadLocalNestingChecker<>();

	private TreeMap<Class<?>, Converter<? super S, ?, ? extends E>> converterMap;

	public DefaultConverterRegistry() {
		super(ConversionComparator.INSTANCE, ConversionService.class);
	}

	protected <T> T get(Class<?> type, TreeMap<Class<?>, T> sourceMap) {
		if (sourceMap == null || sourceMap.isEmpty()) {
			return null;
		}

		T value = sourceMap.get(type);
		if (value != null) {
			return value;
		}

		for (Entry<Class<?>, T> entry : sourceMap.entrySet()) {
			if (type.isAssignableFrom(entry.getKey())) {
				return entry.getValue();
			}
		}
		return null;
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

	@Override
	public Object convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		for (ConversionService service : getServices()) {
			if (NESTING_CHECKERS.isNestingExists(service)) {
				continue;
			}

			Registration registration = NESTING_CHECKERS.registerNestedElement(service);
			try {
				if (service.canConvert(sourceType, targetType)) {
					return service.convert(source, sourceType, targetType);
				}
			} finally {
				registration.unregister();
			}
		}
		return ConverterRegistry.super.convert(source, sourceType, targetType);
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (isConverterRegistred(targetType.getType())) {
			return true;
		}

		for (ConversionService service : getServices()) {
			if (NESTING_CHECKERS.isNestingExists(service)) {
				continue;
			}

			Registration registration = NESTING_CHECKERS.registerNestedElement(service);
			try {
				if (service.canConvert(sourceType, targetType)) {
					return true;
				}
			} finally {
				registration.unregister();
			}
		}
		return false;
	}

}
