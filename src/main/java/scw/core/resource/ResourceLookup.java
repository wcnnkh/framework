package scw.core.resource;

import java.io.InputStream;
import java.util.Collection;

import scw.core.Consumer;
import scw.core.Converter;
import scw.core.Verification;

public interface ResourceLookup {
	boolean lookup(String resource);

	boolean lookup(String resource, Consumer<InputStream> consumer);

	<T> T getResource(String resource, Converter<InputStream, T> converter);

	Collection<Class<?>> getClasses();

	Collection<Class<?>> getClasses(String resource);

	Collection<Class<?>> getClasses(String resource, Verification<String> classNameVerification);
}