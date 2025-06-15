package run.soeasy.framework.io;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingRunnable;

class WriterPipeline extends FilterWriter implements Pipeline<Writer, IOException> {
	@NonNull
	private final ThrowingRunnable<? extends IOException> closeable;
	private final AtomicBoolean closed = new AtomicBoolean();

	public WriterPipeline(Pipeline<? extends Writer, ? extends IOException> pipeline) throws IOException {
		this(pipeline.get(), pipeline::close);
	}

	public WriterPipeline(Writer out, ThrowingRunnable<? extends IOException> closeable) {
		super(out);
		this.closeable = closeable;
	}

	@Override
	public Writer get() throws IOException {
		return out;
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
