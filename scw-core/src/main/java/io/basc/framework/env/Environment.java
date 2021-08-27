package io.basc.framework.env;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.Converter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.resolve.ResourceResolver;
import io.basc.framework.core.Constants;
import io.basc.framework.core.utils.ArrayUtils;
import io.basc.framework.core.utils.StringUtils;
import io.basc.framework.event.EmptyObservable;
import io.basc.framework.event.Observable;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourcePatternResolver;
import io.basc.framework.io.ResourceUtils;
import io.basc.framework.io.event.ObservableProperties;
import io.basc.framework.io.resolver.PropertiesResolver;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.placeholder.PlaceholderReplacer;
import io.basc.framework.util.placeholder.PropertyResolver;
import io.basc.framework.value.PropertyFactory;

import java.nio.charset.Charset;
import java.util.Properties;

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

		// 颠倒一下，优先级高的覆盖优先级低的
		return toObservableProperties(propertiesResolver, charset, (Resource[]) ArrayUtils.reversal(resources));
	}

	default Observable<Properties> toObservableProperties(Resource... resources) {
		return toObservableProperties(getPropertiesResolver(), getCharset(), resources);
	}

	default Observable<Properties> toObservableProperties(PropertiesResolver propertiesResolver,
			@Nullable Charset charset, Resource... resources) {
		ObservableProperties properties = new ObservableProperties();
		Converter<Resource, Properties> converter = propertiesResolver.toPropertiesConverter(charset);
		for (Resource resource : resources) {
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

	ConversionService getConversionService();

	ResourceResolver getResourceResolver();

	Resource[] getResources(String locationPattern);
}