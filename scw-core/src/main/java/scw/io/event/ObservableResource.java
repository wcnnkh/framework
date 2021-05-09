package scw.io.event;

import scw.convert.Converter;
import scw.event.AbstractObservable;
import scw.event.ChangeEvent;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.io.Resource;

public class ObservableResource<T> extends AbstractObservable<T> {
	private final Resource resource;
	private final Converter<Resource, T> converter;
	private final EventRegistration eventRegistration;

	public ObservableResource(Resource resource,
			Converter<Resource, T> converter) {
		this.resource = resource;
		this.converter = converter;
		this.eventRegistration = resource.registerListener(new EventListener<ResourceEvent>() {
			
			@Override
			public void onEvent(ResourceEvent event) {
				publishEvent(new ChangeEvent<T>(event.getEventType(), forceGet()));
			}
		});
	}

	public Resource getResource() {
		return resource;
	}

	public T forceGet() {
		return converter.convert(resource);
	}
	
	@Override
	protected void finalize() throws Throwable {
		eventRegistration.unregister();
		super.finalize();
	}
}
