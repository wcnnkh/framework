package io.basc.framework.env1;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.function.Function;

import io.basc.framework.event.observe.Observable;
import io.basc.framework.event.observe.support.ObservablePropertiesRegistry;
import io.basc.framework.event.observe.support.ObservableResource;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceUtils;
import io.basc.framework.io.resolver.PropertiesResolver;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.element.Elements;

public interface EnvironmentResources extends EnvironmentCapable {
	Elements<Resource> getProfileResources(String location);

	default Observable<Properties> getProperties(PropertiesResolver propertiesResolver, String location)
			throws IOException {
		return getProperties(propertiesResolver, location, (String) null);
	}

	default Observable<Properties> getProperties(PropertiesResolver propertiesResolver, String location,
			@Nullable Charset charset) throws IOException {
		Elements<Resource> resources = getProfileResources(location);
		return toObservableProperties(resources, propertiesResolver, charset);
	}

	default Observable<Properties> getProperties(PropertiesResolver propertiesResolver, String location,
			@Nullable String charsetName) throws IOException {
		return getProperties(propertiesResolver, location,
				StringUtils.isEmpty(charsetName) ? null : Charset.forName(charsetName));
	}

	default Observable<Properties> getProperties(String location) throws IOException {
		return getProperties(getPropertiesResolver(), location);
	}

	default Observable<Properties> getProperties(String location, @Nullable Charset charset) throws IOException {
		return getProperties(getPropertiesResolver(), location, charset);
	}

	default Observable<Properties> getProperties(String location, @Nullable String charsetName) throws IOException {
		return getProperties(getPropertiesResolver(), location, charsetName);
	}

	PropertiesResolver getPropertiesResolver();

	default Observable<Properties> toObservableProperties(Elements<? extends Resource> resources,
			PropertiesResolver propertiesResolver, @Nullable Charset charset) {
		if (resources == null || resources.isEmpty()) {
			return Observable.empty();
		}

		ObservablePropertiesRegistry properties = new ObservablePropertiesRegistry();
		Function<Resource, Properties> converter = ResourceUtils.toPropertiesConverter(propertiesResolver, charset);
		for (Resource resource : resources) {
			properties.register(new ObservableResource(resource).map(converter));
		}
		return properties;
	}
}
