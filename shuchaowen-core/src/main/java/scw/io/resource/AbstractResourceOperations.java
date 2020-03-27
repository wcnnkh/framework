package scw.io.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import scw.core.Consumer;
import scw.core.Converter;
import scw.core.GlobalPropertyFactory;
import scw.io.IOUtils;
import scw.lang.NotFoundException;
import scw.util.FormatUtils;
import scw.util.value.property.PropertyFactory;

public abstract class AbstractResourceOperations implements ResourceOperations {

	public boolean isExist(String resource) {
		return lookup(resource, null);
	}

	public <T> T getResource(String resource, Converter<InputStream, T> converter) {
		return ResourceUtils.getResource(resource, converter, this);
	}

	public Properties getProperties(String path) {
		return getProperties(path, (String) null);
	}

	public Properties getProperties(String path, String charsetName) {
		return getProperties(path, charsetName, GlobalPropertyFactory.getInstance());
	}

	public Properties getProperties(String path, PropertyFactory propertyFactory) {
		return getProperties(path, null, propertyFactory);
	}

	public Properties getProperties(ResourceLookup resourceLookup, String resource, String charsetName,
			PropertyFactory propertyFactory) {
		Properties properties = new Properties();
		resourceLookup.lookup(resource, new LoadPropertiesConsumer(properties, resource, charsetName));
		if (propertyFactory == null) {
			return properties;
		}

		for (Entry<Object, Object> entry : properties.entrySet()) {
			Object value = entry.getValue();
			if (value == null) {
				continue;
			}

			entry.setValue(FormatUtils.format(value.toString(), propertyFactory, true));
		}
		return properties;
	}

	public Properties getProperties(String resource, String charsetName, PropertyFactory propertyFactory) {
		return getProperties(this, resource, charsetName, propertyFactory);
	}

	public final List<String> getFileContentLineList(String path, final String charsetName) {
		return getResource(path, new Converter<InputStream, List<String>>() {

			public List<String> convert(InputStream inputStream) {
				try {
					return IOUtils.readLines(inputStream, charsetName);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}

	public final String getFileContent(String path, final String charsetName) {
		return getResource(path, new Converter<InputStream, String>() {

			public String convert(InputStream inputStream) {
				return IOUtils.readContent(inputStream, charsetName);
			}
		});
	}

	public final void consumterInputStream(String resource, Consumer<InputStream> consumer) {
		if (lookup(resource, consumer)) {
			return;
		}
		throw new NotFoundException(resource);
	}
}
