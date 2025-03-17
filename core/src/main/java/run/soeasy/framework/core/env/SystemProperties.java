package run.soeasy.framework.core.env;

import lombok.NonNull;
import run.soeasy.framework.core.convert.service.ConversionService;
import run.soeasy.framework.core.convert.support.IdentityConversionService;
import run.soeasy.framework.core.convert.transform.stereotype.Properties;
import run.soeasy.framework.core.convert.transform.stereotype.Property;
import run.soeasy.framework.util.collections.Elements;

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
