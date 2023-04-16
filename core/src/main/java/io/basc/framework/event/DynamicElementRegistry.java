package io.basc.framework.event;

import io.basc.framework.util.ElementRegistry;
import io.basc.framework.util.Elements;

public interface DynamicElementRegistry<E> extends ElementRegistry<E> {
	BroadcastEventRegistry<ChangeEvent<Elements<E>>> getElementEventRegistry();
}
