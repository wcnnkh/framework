package scw.io.event;

import scw.event.support.DefaultEventDispatcher;

public class SimpleResourceEventDispatcher extends DefaultEventDispatcher<ResourceEvent>
		implements ResourceEventDispatcher {

	public SimpleResourceEventDispatcher() {
		this(true);
	}

	public SimpleResourceEventDispatcher(boolean concurrent) {
		super(concurrent);
	}
}
