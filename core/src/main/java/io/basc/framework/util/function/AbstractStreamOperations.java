package io.basc.framework.util.function;

import io.basc.framework.util.Endpoint;
import io.basc.framework.util.Processor;

public abstract class AbstractStreamOperations<T, E extends Throwable, C extends AbstractStreamOperations<T, E, C>>
		extends StandardCloser<T, E, C> implements StreamOperations<T, E> {

	public AbstractStreamOperations() {
		this(null, null);
	}

	public AbstractStreamOperations(Processor<? extends E> closeProcessor) {
		this(closeProcessor, null);
	}

	public AbstractStreamOperations(Endpoint<? super T, ? extends E> closeHandler) {
		this(null, closeHandler);
	}

	public AbstractStreamOperations(Processor<? extends E> closeProcessor,
			Endpoint<? super T, ? extends E> closeHandler) {
		super(closeProcessor, closeHandler);
	}
}
