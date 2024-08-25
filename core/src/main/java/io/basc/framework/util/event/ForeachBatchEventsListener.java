package io.basc.framework.util.event;

import io.basc.framework.util.element.Elements;
import lombok.Data;

@Data
public class ForeachBatchEventsListener<E> implements EventListener<Elements<E>> {
	private final EventListener<E> eventListener;

	@Override
	public void onEvent(Elements<E> events) {
		events.forEach((e) -> onEvent(eventListener, e));
	}

	protected void onEvent(EventListener<E> eventListener, E event) {
		eventListener.onEvent(event);
	}
}
