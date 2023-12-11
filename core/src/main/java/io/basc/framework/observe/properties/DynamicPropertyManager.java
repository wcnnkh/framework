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
 * 动态的属性管理
 * 
 * @author wcnnkh
 *
 * @param <K>
 * @param <V>
 */
public class DynamicPropertyManager<K, V> extends MergedObservableMap<K, V> {
	private final Function<? super Properties, ? extends Map<K, V>> propertiesMapper;
	private final ResourceWatcher resourceWatcher = new ResourceWatcher();

	public DynamicPropertyManager(Map<K, V> targetMap,
			Function<? super Properties, ? extends Map<K, V>> propertiesMapper) {
		super(targetMap);
		Assert.requiredArgument(propertiesMapper != null, "propertiesMapper");
		this.propertiesMapper = propertiesMapper;
	}

	public Registration registerObservableProperties(ObservableValue<? extends Properties> observableProperties) {
		return registerObservableValue(observableProperties, propertiesMapper);
	}

	public Registration registerResource(Resource resource) {
		return registerResource(resource, DefaultPropertiesResolver.getInstance());
	}

	public Registration registerResource(Resource resource, PropertiesResolver propertiesResolver) {
		ResourceToObservableProperties observableProperties = new ResourceToObservableProperties(resource,
				propertiesResolver);
		Registration registration = registerObservableProperties(observableProperties);
		registration = registration.and(() -> observableProperties.watch(resourceWatcher));
		return registration;
	}
}
