package scw.io.resource;

import java.io.InputStream;
import java.util.Collection;
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

	public synchronized void addFirst(ResourceLookup resourceLookup) {
		resourceLookups.addFirst(resourceLookup);
	}

	public synchronized void add(ResourceLookup resourceLookup) {
		resourceLookups.add(resourceLookup);
	}

	public synchronized void addAll(Collection<ResourceLookup> resourceLookups) {
		this.resourceLookups.addAll(resourceLookups);
	}
}
