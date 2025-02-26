package io.basc.framework.core.env;

import io.basc.framework.core.convert.service.ConversionService;
import io.basc.framework.core.convert.support.IdentityConversionService;
import io.basc.framework.core.convert.transform.stereotype.Properties;
import io.basc.framework.core.convert.transform.stereotype.Property;
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
	public Elements<Property> getElements() {
		return keys().map((key) -> get(key));
	}

	@Override
	public Elements<String> keys() {
		Elements<String> systemKeys = Elements.of(System.getProperties().stringPropertyNames());
		Elements<String> envKeys = Elements.of(System.getenv().keySet());
		return systemKeys.concat(envKeys).distinct();
	}
}
