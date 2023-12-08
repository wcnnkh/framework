package io.basc.framework.context;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.function.Function;

import io.basc.framework.beans.factory.HierarchicalBeanFactory;
import io.basc.framework.beans.factory.ListableBeanFactory;
import io.basc.framework.env.EnvironmentCapable;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourcePatternResolver;
import io.basc.framework.io.ResourceUtils;
import io.basc.framework.io.resolver.PropertiesResolver;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.ParentDiscover;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.element.Elements;
import io.basc.framework.value.observe.Observable;
import io.basc.framework.value.observe.support.ObservablePropertiesRegistry;
import io.basc.framework.value.observe.support.ObservableResource;

public interface ApplicationContext extends EnvironmentCapable, ClassLoaderProvider, ParentDiscover<ApplicationContext>,
		ResourcePatternResolver, ListableBeanFactory, HierarchicalBeanFactory {
	/**
	 * Return the unique id of this application context.
	 * 
	 * @return the unique id of the context, or {@code null} if none
	 */
	/*
	 * @Nullable String getId();
	 * 
	 *//**
		 * Return a name for the deployed application that this context belongs to.
		 * 
		 * @return a name for the deployed application, or the empty String by default
		 */
	/*
	 * String getApplicationName();
	 * 
	 *//**
		 * Return a friendly name for this context.
		 * 
		 * @return a display name for this context (never {@code null})
		 *//*
			 * String getDisplayName();
			 */

	/**
	 * Return the parent context, or {@code null} if there is no parent and this is
	 * the root of the context hierarchy.
	 * 
	 * @return the parent context, or {@code null} if there is no parent
	 */
	@Nullable
	ApplicationContext getParent();

	default Elements<Resource> getProfileResources(String location) {
		Resource rootResource = getResource(location);
		Elements<Resource> root = Elements.singleton(rootResource);
		Elements<String> profiles = getEnvironment().getProfiles(location);
		if (profiles.isEmpty()) {
			return root;
		}

		Elements<Resource> resourceProfiles = profiles.map((name) -> getResource(name));
		return root.concat(resourceProfiles);
	}

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