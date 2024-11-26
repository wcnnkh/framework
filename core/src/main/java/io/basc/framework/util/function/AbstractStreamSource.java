package io.basc.framework.util.function;

import io.basc.framework.util.Endpoint;
import io.basc.framework.util.Processor;

public abstract class AbstractStreamSource<T, E extends Throwable, C extends AbstractStreamSource<T, E, C>>
		extends StandardCloser<T, E, C> implements StreamSource<T, E> {

	public AbstractStreamSource() {
		this(null, null);
	}

	public AbstractStreamSource(Processor<? extends E> closeProcessor) {
		this(closeProcessor, null);
	}

	public AbstractStreamSource(Endpoint<? super T, ? extends E> closeHandler) {
		this(null, closeHandler);
	}

	public AbstractStreamSource(Processor<? extends E> closeProcessor,
			Endpoint<? super T, ? extends E> closeHandler) {
		super(closeProcessor, closeHandler);
	}
}
