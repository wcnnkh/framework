package io.basc.framework.core.env;

import io.basc.framework.core.convert.config.ConversionService;
import io.basc.framework.core.convert.support.IdentityConversionService;
import io.basc.framework.core.mapping.Properties;
import io.basc.framework.core.mapping.Property;
import io.basc.framework.util.collections.Elements;
import lombok.NonNull;

public class SystemProperties implements Properties {
	@NonNull
	private final ConversionService conversionService = new IdentityConversionService();

	@Override
	public Property get(String key) {
		return new SystemProperty(key, conversionService);
	}

	@Override
	public Elements<String> keys() {
		Elements<String> systemKeys = Elements.of(System.getProperties().stringPropertyNames());
		Elements<String> envKeys = Elements.of(System.getenv().keySet());
		return systemKeys.concat(envKeys).distinct();
	}
}
