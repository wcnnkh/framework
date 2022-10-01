package io.basc.framework.env;

import java.nio.charset.Charset;
import java.util.Properties;
import java.util.function.Function;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.resolve.ResourceResolver;
import io.basc.framework.event.EmptyObservable;
import io.basc.framework.event.Observable;
import io.basc.framework.factory.BeanFactory;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceUtils;
import io.basc.framework.io.event.ObservableProperties;
import io.basc.framework.io.resolver.PropertiesResolver;
import io.basc.framework.lang.Constants;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.placeholder.PlaceholderFormat;
import io.basc.framework.util.placeholder.PlaceholderReplacer;

public interface Environment extends BeanFactory, PlaceholderFormat {
	public static final String CHARSET_PROPERTY = "io.basc.framework.charset.name";
	public static final String WORK_PATH_PROPERTY = "io.basc.framework.work.path";

	default Charset getCharset() {
		return getProperties().getValue(CHARSET_PROPERTY, Charset.class, Constants.UTF_8);
	}

	default String getCharsetName() {
		return getProperties().getValue(CHARSET_PROPERTY, String.class, Constants.UTF_8_NAME);
	}

	ConversionService getConversionService();

	default Observable<Charset> getObservableCharset() {
		return getProperties().getObservableValue(CHARSET_PROPERTY, Charset.class, Constants.UTF_8);
	}

	default Observable<String> getObservableCharsetName() {
		return getProperties().getObservableValue(CHARSET_PROPERTY, String.class, Constants.UTF_8_NAME);
	}

	default Observable<String> getObservableWorkPath() {
		return getProperties().getObservableValue(WORK_PATH_PROPERTY, String.class, null);
	}

	PlaceholderReplacer getPlaceholderReplacer();

	default Observable<Properties> getProperties(PropertiesResolver propertiesResolver, String location) {
		return getProperties(propertiesResolver, location, (String) null);
	}

	default Observable<Properties> getProperties(PropertiesResolver propertiesResolver, String location,
			@Nullable Charset charset) {
		Resource[] resources = getResourceLoader().getResources(location);
		if (ArrayUtils.isEmpty(resources)) {
			return new EmptyObservable<Properties>();
		}

		// 颠倒一下，优先级高的覆盖优先级低的
		return toObservableProperties(propertiesResolver, charset, (Resource[]) ArrayUtils.reversal(resources));
	}

	default Observable<Properties> getProperties(PropertiesResolver propertiesResolver, String location,
			@Nullable String charsetName) {
		return getProperties(propertiesResolver, location,
				StringUtils.isEmpty(charsetName) ? null : Charset.forName(charsetName));
	}

	default Observable<Properties> getProperties(String location) {
		return getProperties(getPropertiesResolver(), location);
	}

	default Observable<Properties> getProperties(String location, @Nullable Charset charset) {
		return getProperties(getPropertiesResolver(), location, charset);
	}

	default Observable<Properties> getProperties(String location, @Nullable String charsetName) {
		return getProperties(getPropertiesResolver(), location, charsetName);
	}

	PropertiesResolver getPropertiesResolver();

	EnvironmentProperties getProperties();

	EnvironmentResourceLoader getResourceLoader();

	ResourceResolver getResourceResolver();

	default String getWorkPath() {
		return getProperties().getString(WORK_PATH_PROPERTY);
	}

	/**
	 * 解析并替换文本
	 */
	@Override
	default String replacePlaceholders(String text) {
		return getPlaceholderReplacer().replacePlaceholders(text, (name) -> getProperties().getString(name));
	}

	@Override
	default String replaceRequiredPlaceholders(String text) throws IllegalArgumentException {
		return getPlaceholderReplacer().replaceRequiredPlaceholders(text, (name) -> getProperties().getString(name));
	}

	default Object resolveResource(String location, TypeDescriptor targetType) {
		Resource resource = getResourceLoader().getResource(location);
		if (resource == null || !resource.exists()) {
			return null;
		}

		return getResourceResolver().resolveResource(resource, targetType);
	}

	default Observable<Properties> toObservableProperties(PropertiesResolver propertiesResolver,
			@Nullable Charset charset, Resource... resources) {
		ObservableProperties properties = new ObservableProperties();
		Function<Resource, Properties> converter = ResourceUtils.toPropertiesConverter(propertiesResolver, charset);
		for (Resource resource : resources) {
			properties.combine(resource, converter);
		}
		return properties;
	}

	default Observable<Properties> toObservableProperties(Resource... resources) {
		return toObservableProperties(getPropertiesResolver(), getCharset(), resources);
	}
}