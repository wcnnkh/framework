package io.basc.framework.observe.properties;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import io.basc.framework.io.Resource;
import io.basc.framework.io.resolver.DefaultPropertiesResolver;
import io.basc.framework.io.resolver.PropertiesResolver;
import io.basc.framework.lang.Nullable;
import io.basc.framework.observe.value.ObservableValue;
import io.basc.framework.observe.watch.ResourceObserver;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Registration;

/**
 * 动态的map
 * 
 * @author wcnnkh
 *
 * @param <K>
 * @param <V>
 */
public class DynamicMap<K, V> extends MergedObservableMap<K, V> {
	private final Function<? super Properties, ? extends Map<K, V>> propertiesMapper;

	private final ResourceObserver resourceWatcher = new ResourceObserver();

	public DynamicMap(Map<K, V> targetMap, Function<? super Properties, ? extends Map<K, V>> propertiesMapper) {
		super(targetMap);
		Assert.requiredArgument(propertiesMapper != null, "propertiesMapper");
		this.propertiesMapper = propertiesMapper;
	}

	public Function<? super Properties, ? extends Map<K, V>> getPropertiesMapper() {
		return propertiesMapper;
	}

	public ResourceObserver getResourceWatcher() {
		return resourceWatcher;
	}

	public Registration registerObservableProperties(ObservableProperties observableProperties) {
		return registerProperties(observableProperties.asObservableValue());
	}

	public Registration registerProperties(ObservableValue<? extends Properties> properties) {
		return registerValue(properties, propertiesMapper);
	}

	public final Registration registerResource(Resource resource) {
		return registerResource(resource, null);
	}

	public final Registration registerResource(Resource resource, @Nullable Charset charset) {
		return registerResource(resource, DefaultPropertiesResolver.getInstance(), charset);
	}

	public final Registration registerResource(Resource resource, PropertiesResolver propertiesResolver,
			@Nullable Charset charset) {
		ObservableProperties observableProperties = new ObservableProperties();
		if (charset != null) {
			observableProperties.setCharset(charset);
		}
		Registration registration = observableProperties.bind(resource, resourceWatcher);
		return registration.and(registerObservableProperties(observableProperties));
	}
}
