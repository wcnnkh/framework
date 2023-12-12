package io.basc.framework.observe.properties;

import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import io.basc.framework.io.Resource;
import io.basc.framework.io.resolver.DefaultPropertiesResolver;
import io.basc.framework.io.resolver.PropertiesResolver;
import io.basc.framework.observe.value.ObservableValue;
import io.basc.framework.observe.watch.ResourceWatcher;
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

	private final ResourceWatcher resourceWatcher = new ResourceWatcher();

	public DynamicMap(Map<K, V> targetMap, Function<? super Properties, ? extends Map<K, V>> propertiesMapper) {
		super(targetMap);
		Assert.requiredArgument(propertiesMapper != null, "propertiesMapper");
		this.propertiesMapper = propertiesMapper;
	}

	public Function<? super Properties, ? extends Map<K, V>> getPropertiesMapper() {
		return propertiesMapper;
	}

	public ResourceWatcher getResourceWatcher() {
		return resourceWatcher;
	}

	public Registration registerObservableProperties(ObservableProperties observableProperties) {
		return registerProperties(observableProperties.asObservableValue());
	}

	public Registration registerProperties(ObservableValue<? extends Properties> properties) {
		return registerValue(properties, propertiesMapper);
	}

	public final Registration registerResource(Resource resource) {
		return registerResource(resource, DefaultPropertiesResolver.getInstance());
	}

	public final Registration registerResource(Resource resource, PropertiesResolver propertiesResolver) {
		ObservableProperties observableProperties = new ObservableProperties();
		Registration registration = observableProperties.bind(resource, resourceWatcher);
		return registration.and(registerObservableProperties(observableProperties));
	}
}
