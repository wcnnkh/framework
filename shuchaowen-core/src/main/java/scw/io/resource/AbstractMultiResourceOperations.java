package scw.io.resource;

import java.io.InputStream;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

import scw.util.queue.Consumer;
import scw.util.value.property.PropertyFactory;

public abstract class AbstractMultiResourceOperations extends AbstractResourceOperations {

	public abstract List<String> getResourceNameList(String resource);

	public abstract ResourceLookup getTargetResourceLookup();

	public boolean lookup(String resource, Consumer<InputStream> consumer) {
		List<String> resourceNameList = getResourceNameList(resource);
		for (String name : resourceNameList) {
			if (getTargetResourceLookup().lookup(name, consumer)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Properties getProperties(String resource, String charsetName, PropertyFactory propertyFactory) {
		List<String> resourceNameList = getResourceNameList(resource);
		ListIterator<String> iterator = resourceNameList.listIterator(resourceNameList.size());
		Properties properties = new Properties();
		while (iterator.hasPrevious()) {
			String name = iterator.previous();
			Properties p = getProperties(getTargetResourceLookup(), name, charsetName, propertyFactory);
			properties.putAll(p);
		}
		return properties;
	}
}
