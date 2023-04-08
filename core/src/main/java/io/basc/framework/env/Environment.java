package io.basc.framework.env;

import java.nio.charset.Charset;
import java.util.Properties;
import java.util.function.Function;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.resolve.ResourceResolver;
import io.basc.framework.event.Observable;
import io.basc.framework.event.support.ObservablePropertiesRegistry;
import io.basc.framework.event.support.ObservableResource;
import io.basc.framework.factory.BeanFactory;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceUtils;
import io.basc.framework.io.resolver.PropertiesResolver;
import io.basc.framework.lang.Constants;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.ServiceLoader;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.placeholder.PlaceholderFormat;
import io.basc.framework.util.placeholder.PlaceholderReplacer;

public interface Environment extends BeanFactory, PlaceholderFormat {
	public static final String CHARSET_PROPERTY = "io.basc.framework.charset.name";
	public static final String WORK_PATH_PROPERTY = "io.basc.framework.work.path";

	default Charset getCharset() {
		return getProperties().get(CHARSET_PROPERTY).as(Charset.class).orElse(Constants.UTF_8);
	}

	default String getCharsetName() {
		return getProperties().get(CHARSET_PROPERTY).as(String.class).orElse(Constants.UTF_8_NAME);
	}

	ServiceLoader<Resource> getResources();

	ConversionService getConversionService();

	default Observable<Charset> getObservableCharset() {
		return getProperties().getObservable(CHARSET_PROPERTY)
				.map((e) -> e.or(Constants.UTF_8).getAsObject(Charset.class));
	}

	default Observable<String> getObservableCharsetName() {
		return getProperties().getObservable(CHARSET_PROPERTY).map((e) -> e.or(Constants.UTF_8_NAME).getAsString());
	}

	default Observable<String> getObservableWorkPath() {
		return getProperties().getObservable(WORK_PATH_PROPERTY).map((e) -> e.getAsString());
	}

	PlaceholderReplacer getPlaceholderReplacer();

	default Observable<Properties> getProperties(PropertiesResolver propertiesResolver, String location) {
		return getProperties(propertiesResolver, location, (String) null);
	}

	default Observable<Properties> getProperties(PropertiesResolver propertiesResolver, String location,
			@Nullable Charset charset) {
		Resource[] resources = getResourceLoader().getResources(location);
		if (ArrayUtils.isEmpty(resources)) {
			return Observable.empty();
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
		return getProperties().getAsString(WORK_PATH_PROPERTY);
	}

	/**
	 * 解析并替换文本
	 */
	@Override
	default String replacePlaceholders(String text) {
		return getPlaceholderReplacer().replacePlaceholders(text, (name) -> getProperties().getAsString(name));
	}

	@Override
	default String replaceRequiredPlaceholders(String text) throws IllegalArgumentException {
		return getPlaceholderReplacer().replaceRequiredPlaceholders(text, (name) -> getProperties().getAsString(name));
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
		ObservablePropertiesRegistry properties = new ObservablePropertiesRegistry();
		Function<Resource, Properties> converter = ResourceUtils.toPropertiesConverter(propertiesResolver, charset);
		for (Resource resource : resources) {
			properties.register(new ObservableResource(resource).map(converter));
		}
		return properties;
	}

	default Observable<Properties> toObservableProperties(Resource... resources) {
		return toObservableProperties(getPropertiesResolver(), getCharset(), resources);
	}
}