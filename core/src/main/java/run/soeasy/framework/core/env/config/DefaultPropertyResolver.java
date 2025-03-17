package run.soeasy.framework.core.env.config;

import java.util.concurrent.ConcurrentHashMap;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.transform.stereotype.collection.MapProperties;

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
