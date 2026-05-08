package run.soeasy.framework.core.collection;

import java.util.Iterator;

import lombok.NonNull;

class CloseableIteratorImpl<E, W extends Iterator<E>> implements CloseableIterator<E>, IteratorWrapper<E, W> {
	@NonNull
	private final W source;
	private Runnable closeHandler;
	private boolean closed;

	public CloseableIteratorImpl(W source) {
		this(source, null);
	}

	public CloseableIteratorImpl(@NonNull W source, Runnable closeHandler) {
		this.source = source;
		this.closeHandler = closeHandler;
	}

	@Override
	public void close() {
		if (closed) {
			return;
		}

		try {
			if (closeHandler != null) {
				closeHandler.run();
			}
		} finally {
			closed = true;
		}
	}

	@Override
	public W getSource() {
		return getSource();
	}
}
