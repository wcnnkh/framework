package io.basc.framework.value;

import io.basc.framework.event.broadcast.BroadcastEventDispatcher;
import io.basc.framework.event.broadcast.BroadcastEventRegistry;
import io.basc.framework.event.broadcast.support.StandardBroadcastEventDispatcher;
import io.basc.framework.observe.ObservableEvent;
import io.basc.framework.util.element.Elements;

public abstract class AbstractEditablePropertyFactory implements EditablePropertyFactory {
	private final BroadcastEventDispatcher<ObservableEvent<Elements<String>>> keyEventDispatcher = new StandardBroadcastEventDispatcher<>();

	public BroadcastEventDispatcher<ObservableEvent<Elements<String>>> getKeyEventDispatcher() {
		return keyEventDispatcher;
	}

	@Override
	public BroadcastEventRegistry<ObservableEvent<Elements<String>>> getKeyEventRegistry() {
		return keyEventDispatcher;
	}
}
