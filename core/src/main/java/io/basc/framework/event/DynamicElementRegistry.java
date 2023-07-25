package io.basc.framework.event;

import io.basc.framework.event.broadcast.BroadcastEventRegistry;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.registry.ElementRegistry;

public interface DynamicElementRegistry<E> extends ElementRegistry<E> {
	BroadcastEventRegistry<ChangeEvent<Elements<E>>> getElementEventRegistry();
}
