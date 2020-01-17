package scw.io.resource;

import java.io.InputStream;

import scw.core.Consumer;

public interface ResourceLookup {
	boolean lookup(String resource, Consumer<InputStream> consumer);
}
