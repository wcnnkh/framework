package scw.io.event;

import scw.core.Converter;
import scw.io.Resource;

public class ObservableResource<T> extends AbstractObservableResource<T> {
	private final Resource resource;
	private final Converter<Resource, T> converter;

	public ObservableResource(Resource resource,
			Converter<Resource, T> converter) {
		this.resource = resource;
		this.converter = converter;
	}

	@Override
	public Resource getResource() {
		return resource;
	}

	public T forceGet() {
		return converter.convert(resource);
	}
}
