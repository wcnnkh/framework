package run.soeasy.framework.io;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingRunnable;

class ReaderPipeline extends FilterReader implements Pipeline<Reader, IOException> {
	@NonNull
	private final ThrowingRunnable<? extends IOException> closeable;
	private final AtomicBoolean closed = new AtomicBoolean();

	public ReaderPipeline(Pipeline<? extends Reader, ? extends IOException> pipeline) throws IOException {
		this(pipeline.get(), pipeline::close);
	}

	public ReaderPipeline(Reader in, ThrowingRunnable<? extends IOException> closeable) {
		super(in);
		this.closeable = closeable;
	}

	@Override
	public Reader get() throws IOException {
		return in;
	}

	@Override
	public boolean isClosed() {
		return closed.get();
	}

	@Override
	public void close() throws IOException {
		if (closed.compareAndSet(false, true)) {
			try {
				super.close();
			} finally {
				closeable.run();
			}
		}

	}

}
