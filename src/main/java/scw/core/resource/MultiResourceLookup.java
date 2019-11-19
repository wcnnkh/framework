package scw.core.resource;

import java.io.InputStream;
import java.util.LinkedList;

import scw.core.Consumer;

public final class MultiResourceLookup implements ResourceLookup {
	private final LinkedList<ResourceLookup> resourceLookups = new LinkedList<ResourceLookup>();

	public boolean lookup(String resource, Consumer<InputStream> consumer) {
		for (ResourceLookup resourceLookup : resourceLookups) {
			if (resourceLookup.lookup(resource, consumer)) {
				return true;
			}
		}
		return false;
	}

	public void addResourceLookup(ResourceLookup resourceLookup) {
		resourceLookups.add(resourceLookup);
	}
}
