package io.basc.framework.util.event.convert;

import java.util.function.Function;

import io.basc.framework.util.element.Elements;
import io.basc.framework.util.event.EventListener;
import io.basc.framework.util.event.batch.BatchEventListener;
import lombok.NonNull;

public class ConvertibleBatchEventListener<S, E> extends ConvertibleEventListener<Elements<S>, Elements<E>>
		implements BatchEventListener<E> {

	public ConvertibleBatchEventListener(@NonNull EventListener<Elements<S>> eventListener,
			@NonNull Function<? super Elements<E>, ? extends Elements<S>> converter) {
		super(eventListener, converter);
	}

}
