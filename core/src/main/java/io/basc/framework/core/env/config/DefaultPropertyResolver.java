package io.basc.framework.core.env.config;

import java.util.concurrent.ConcurrentHashMap;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.mapping.collection.MapProperties;
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

	public void setParentPropertyResolver(ConfigurablePropertyResolver parentPropertyResolver) {

	}
}
