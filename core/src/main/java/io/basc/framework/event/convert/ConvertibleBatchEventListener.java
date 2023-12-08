package io.basc.framework.event.convert;

import java.util.function.Function;

import io.basc.framework.event.EventListener;
import io.basc.framework.event.batch.BatchEventListener;
import io.basc.framework.util.element.Elements;
import lombok.NonNull;

public class ConvertibleBatchEventListener<S, E> extends ConvertibleEventListener<Elements<S>, Elements<E>>
		implements BatchEventListener<E> {

	public ConvertibleBatchEventListener(@NonNull EventListener<Elements<S>> eventListener,
			@NonNull Function<? super Elements<E>, ? extends Elements<S>> converter) {
		super(eventListener, converter);
	}

}
