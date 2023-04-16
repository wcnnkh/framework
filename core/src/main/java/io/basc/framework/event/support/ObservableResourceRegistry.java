package io.basc.framework.event.support;

import io.basc.framework.event.Observable;
import io.basc.framework.io.NonexistentResource;
import io.basc.framework.io.Resource;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ElementRegistration;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Registration;
import io.basc.framework.util.Registrations;
import io.basc.framework.util.Selector;

public class ObservableResourceRegistry extends ObservableRegistry<Resource> {
	private static final Selector<Resource> SELECTOR = (elements) -> {
		for (Resource resource : elements) {
			if (resource != null && resource.exists()) {
				return resource;
			}
		}
		return null;
	};

	public ObservableResourceRegistry() {
		setSelector(SELECTOR);
	}

	public Resource getResource() {
		return orElse(NonexistentResource.INSTANCE);
	}

	@Override
	public void setSelector(Selector<Resource> selector) {
		Assert.requiredArgument(selector != null, "selector");
		super.setSelector(selector);
	}

	public final Registration registerResource(Resource resource) {
		return register(new ObservableResource(resource));
	}

	public final Registrations<ElementRegistration<Observable<? extends Resource>>> registerResources(
			Iterable<? extends Resource> resources) {
		Elements<Observable<Resource>> elements = Elements.of(resources).map((e) -> new ObservableResource(e));
		return registers(elements);
	}
}
