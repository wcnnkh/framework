package scw.env;

import java.nio.charset.Charset;
import java.util.Properties;

import scw.aop.ProxyFactory;
import scw.convert.ConversionService;
import scw.convert.Converter;
import scw.convert.TypeDescriptor;
import scw.convert.resolve.ResourceResolver;
import scw.core.Constants;
import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;
import scw.event.EmptyObservable;
import scw.event.Observable;
import scw.io.Resource;
import scw.io.ResourcePatternResolver;
import scw.io.ResourceUtils;
import scw.io.event.ObservableProperties;
import scw.io.resolver.PropertiesResolver;
import scw.lang.Nullable;
import scw.util.placeholder.PlaceholderReplacer;
import scw.util.placeholder.PropertyResolver;
import scw.value.PropertyFactory;

public interface Environment extends ResourcePatternResolver, PropertyFactory, PropertyResolver {
	public static final String CHARSET_PROPERTY = "charset.name";
	public static final String WORK_PATH_PROPERTY = "work.path";

	default String getWorkPath() {
		return getString(WORK_PATH_PROPERTY);
	}

	default Observable<String> getObservableWorkPath() {
		return getObservableValue(WORK_PATH_PROPERTY, String.class, null);
	}

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

		return getResourceResolver().resolveResource(resource, targetType);
	}

	default Resource getResource(String location) {
		Resource[] resources = getResources(location);
		if (ArrayUtils.isEmpty(resources)) {
			return null;
		}

		Resource resourceToUse = resources[resources.length - 1];
		for (Resource resource : resources) {
			if (resource.exists()) {
				resourceToUse = resource;
				break;
			}
		}
		return resourceToUse;
	}

	/**
	 * 资源是否存在
	 * 
	 * @see Resource#exists()
	 * @see #getResource(String)
	 * @param location
	 * @return
	 */
	default boolean exists(String location) {
		return ResourceUtils.exists(this, location);
	}

	default Observable<Properties> getProperties(String location) {
		return getProperties(getPropertiesResolver(), location);
	}

	default Observable<Properties> getProperties(String location, @Nullable String charsetName) {
		return getProperties(getPropertiesResolver(), location, charsetName);
	}

	default Observable<Properties> getProperties(String location, @Nullable Charset charset) {
		return getProperties(getPropertiesResolver(), location, charset);
	}

	default Observable<Properties> getProperties(PropertiesResolver propertiesResolver, String location) {
		return getProperties(getPropertiesResolver(), location, (String) null);
	}

	default Observable<Properties> getProperties(PropertiesResolver propertiesResolver, String location,
			@Nullable String charsetName) {
		return getProperties(propertiesResolver, location,
				StringUtils.isEmpty(charsetName) ? null : Charset.forName(charsetName));
	}

	default Observable<Properties> getProperties(PropertiesResolver propertiesResolver, String location,
			@Nullable Charset charset) {
		Resource[] resources = getResources(location);
		if (ArrayUtils.isEmpty(resources)) {
			return new EmptyObservable<Properties>();
		}

		ObservableProperties properties = new ObservableProperties();
		Converter<Resource, Properties> converter = propertiesResolver.toPropertiesConverter(charset);
		// 颠倒一下，优先级高的覆盖优先级低的
		for (Resource resource : (Resource[]) ArrayUtils.reversal(resources)) {
			properties.combine(resource, converter);
		}
		return properties;
	}

	@Override
	default String resolvePlaceholders(String text) {
		return getPlaceholderReplacer().replacePlaceholders(text, this);
	}

	@Override
	default String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
		return getPlaceholderReplacer().replaceRequiredPlaceholders(text, this);
	}

	PlaceholderReplacer getPlaceholderReplacer();

	PropertiesResolver getPropertiesResolver();

	ProxyFactory getProxyFactory();

	ConversionService getConversionService();

	ResourceResolver getResourceResolver();

	Resource[] getResources(String locationPattern);
}