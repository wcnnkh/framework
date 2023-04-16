package io.basc.framework.value;

import io.basc.framework.event.BroadcastEventDispatcher;
import io.basc.framework.event.BroadcastEventRegistry;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.support.StandardBroadcastEventDispatcher;
import io.basc.framework.util.Elements;

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
