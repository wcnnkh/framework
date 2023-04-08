package io.basc.framework.event.support;

import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistrationException;
import io.basc.framework.event.Observable;
import io.basc.framework.io.Resource;
import io.basc.framework.util.Registration;
import lombok.Data;

@Data
public class ObservableResource implements Observable<Resource> {
	private final Resource resource;

	public ObservableResource(Resource resource) {
		this.resource = resource;
	}

	@Override
	public Resource orElse(Resource other) {
		return (resource != null && resource.exists()) ? resource : other;
	}

	@Override
	public Registration registerListener(EventListener<ChangeEvent<Resource>> eventListener)
			throws EventRegistrationException {
		return resource.registerListener(eventListener);
	}
}
