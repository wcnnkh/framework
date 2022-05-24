package io.basc.framework.io.event;

import io.basc.framework.event.AbstractObservable;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventRegistration;
import io.basc.framework.io.Resource;
import io.basc.framework.util.stream.Processor;

public class ObservableResource<T> extends AbstractObservable<T> implements AutoCloseable {
	private final Resource resource;
	private final Processor<Resource, T, ? extends RuntimeException> processor;
	private final EventRegistration eventRegistration;

	public ObservableResource(Resource resource, Processor<Resource, T, ? extends RuntimeException> processor) {
		this.resource = resource;
		this.processor = processor;
		this.eventRegistration = resource
				.registerListener((event) -> publishEvent(new ChangeEvent<T>(event.getEventType(), forceGet())));
	}

	public Resource getResource() {
		return resource;
	}

	public T forceGet() {
		return processor.process(resource);
	}

	@Override
	public void close() {
		eventRegistration.unregister();
	}
}
