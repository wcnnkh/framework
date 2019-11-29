package scw.core.resource;

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

	public void add(ResourceLookup resourceLookup) {
		resourceLookups.add(resourceLookup);
	}
	
	public void addAll(Collection<ResourceLookup> resourceLookups){
		resourceLookups.addAll(resourceLookups);
	}
}
