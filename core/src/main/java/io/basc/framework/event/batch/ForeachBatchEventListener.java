package io.basc.framework.event.batch;

import io.basc.framework.event.EventListener;
import io.basc.framework.util.element.Elements;
import lombok.Data;

@Data
public class ForeachBatchEventListener<E> implements BatchEventListener<E> {
	private final EventListener<E> eventListener;

	@Override
	public void onEvent(Elements<E> events) {
		events.forEach((e) -> onEvent(eventListener, e));
	}

	protected void onEvent(EventListener<E> eventListener, E event) {
		eventListener.onEvent(event);
	}
}
