package scw.io.event;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import scw.io.Resource;
import scw.io.resolver.PropertiesResolver;

public class ObservableProperties extends
		AbstractObservableResources<Properties> {
	private final Collection<Resource> resources;
	private final Charset charset;
	private final PropertiesResolver propertiesResolver;

	/**
	 * @param propertiesResolver
	 * @param resources 传入多个资源，后面的会覆盖前面的
	 * @param charset
	 */
	public ObservableProperties(PropertiesResolver propertiesResolver, Resource[] resources,
			Charset charset) {
		this(propertiesResolver, Arrays.asList(resources), charset);
	}
	
	/**
	 * @param propertiesResolver
	 * @param resources 传入多个资源，后面的会覆盖前面的
	 * @param charsetName
	 */
	public ObservableProperties(PropertiesResolver propertiesResolver, Collection<Resource> resources,
			Charset charset) {
		this.resources = resources;
		this.propertiesResolver = propertiesResolver;
		this.charset = charset;
	}

	@Override
	public Collection<Resource> getResources() {
		return resources;
	}

	public Properties forceGet() {
		Properties properties = new Properties();
		for (Resource resource : resources) {
			propertiesResolver.resolveProperties(properties, resource, charset);
		}
		return properties;
	}
}
