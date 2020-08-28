package scw.io.event;

import scw.event.support.DefaultBasicEventDispatcher;

public class SimpleResourceEventDispatcher extends DefaultBasicEventDispatcher<ResourceEvent>
		implements ResourceEventDispatcher {

	public SimpleResourceEventDispatcher() {
		this(true);
	}

	public SimpleResourceEventDispatcher(boolean concurrent) {
		super(concurrent);
	}
}
