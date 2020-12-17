package scw.io.event;

import java.util.Collection;
import java.util.Properties;

import scw.io.Resource;
import scw.io.ResourceUtils;

public class ObservableProperties extends
		AbstractObservableResources<Properties> {
	private final Collection<Resource> resources;
	private final String charsetName;

	/**
	 * @param resources 传入多个资源，后面的会覆盖前面的
	 * @param charsetName
	 */
	public ObservableProperties(Collection<Resource> resources,
			String charsetName) {
		this.resources = resources;
		this.charsetName = charsetName;
	}

	@Override
	public Collection<Resource> getResources() {
		return resources;
	}

	public Properties forceGet() {
		Properties properties = new Properties();
		for (Resource resource : resources) {
			ResourceUtils.loadProperties(properties, resource, charsetName);
		}
		return properties;
	}
}
