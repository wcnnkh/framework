package scw.core.resource;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import scw.core.Consumer;
import scw.core.Converter;
import scw.core.PropertyFactory;

public interface ResourceOperations extends ResourceLookup{
	boolean isExist(String resource);

	<T> T getResource(String resource, final Converter<InputStream, T> converter);

	Properties getProperties(final String path);

	Properties getProperties(final String path, final String charsetName);

	Properties getProperties(final String path, PropertyFactory propertyFactory);

	Properties getProperties(final String resource, final String charsetName, PropertyFactory propertyFactory);

	List<String> getFileContentLineList(String path, final String charsetName);

	String getFileContent(String path, final String charsetName);
	
	void consumterInputStream(String resource, Consumer<InputStream> consumer);
}
