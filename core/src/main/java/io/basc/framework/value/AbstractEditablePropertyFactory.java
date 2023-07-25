package io.basc.framework.value;

import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.broadcast.BroadcastEventDispatcher;
import io.basc.framework.event.broadcast.BroadcastEventRegistry;
import io.basc.framework.event.broadcast.support.StandardBroadcastEventDispatcher;
import io.basc.framework.util.element.Elements;

public abstract class AbstractEditablePropertyFactory implements EditablePropertyFactory {
	private final BroadcastEventDispatcher<ChangeEvent<Elements<String>>> keyEventDispatcher = new StandardBroadcastEventDispatcher<>();

	public BroadcastEventDispatcher<ChangeEvent<Elements<String>>> getKeyEventDispatcher() {
		return keyEventDispatcher;
	}

	@Override
	public BroadcastEventRegistry<ChangeEvent<Elements<String>>> getKeyEventRegistry() {
		return keyEventDispatcher;
	}
}
