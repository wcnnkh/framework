package io.basc.framework.event.batch;

import io.basc.framework.event.EventListener;
import io.basc.framework.util.element.Elements;

public interface BatchEventListener<E> extends EventListener<Elements<E>> {
	@Override
	void onEvent(Elements<E> events);
}
