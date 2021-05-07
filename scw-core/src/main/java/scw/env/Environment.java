package scw.env;

import java.nio.charset.Charset;

import scw.aop.ProxyFactory;
import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.convert.resolve.ResourceResolver;
import scw.core.Constants;
import scw.event.Observable;
import scw.io.Resource;

public interface Environment extends BasicEnvironment,
		EnvironmentResourceLoader, ConversionService, ResourceResolver,
		ProxyFactory {
	public static final String CHARSET_PROPERTY = "charset.name";

	default String getCharsetName() {
		return getValue(CHARSET_PROPERTY, String.class, Constants.UTF_8_NAME);
	}
	
	default Observable<String> getObservableCharsetName() {
		return getObservableValue(CHARSET_PROPERTY, String.class, Constants.UTF_8_NAME);
	}
	
	default Charset getCharset() {
		return getValue(CHARSET_PROPERTY, Charset.class, Constants.UTF_8);
	}
	
	default Observable<Charset> getObservableCharset() {
		return getObservableValue(CHARSET_PROPERTY, Charset.class, Constants.UTF_8);
	}

	default Object resolveResource(String location, TypeDescriptor targetType) {
		Resource resource = getResource(location);
		if (resource == null || !resource.exists()) {
			return null;
		}

		return resolveResource(resource, targetType);
	}
}