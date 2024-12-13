package io.basc.framework.core.env;

import java.util.concurrent.ConcurrentHashMap;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.collection.MapProperties;
import lombok.NonNull;

public class DefaultPropertyResolver extends MapProperties implements ConfigurablePropertyResolver {
	private final ConfigurableConversionService conversionService;
	private final ConfigurablePlaceholderResolver placeholderResolver = new ConfigurablePlaceholderResolver();

	public DefaultPropertyResolver() {
		this(new ConfigurableConversionService());
	}

	private DefaultPropertyResolver(@NonNull ConfigurableConversionService conversionService) {
		super(new ConcurrentHashMap<>(), TypeDescriptor.map(ConcurrentHashMap.class, Object.class, Object.class),
				conversionService);
		this.conversionService = conversionService;
	}

	@Override
	public ConfigurableConversionService getConversionService() {
		return conversionService;
	}

	@Override
	public ConfigurablePlaceholderResolver getPlaceholderReplacer() {
		return placeholderResolver;
	}
}
