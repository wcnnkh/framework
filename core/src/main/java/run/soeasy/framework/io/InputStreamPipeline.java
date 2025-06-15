package run.soeasy.framework.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingRunnable;

class InputStreamPipeline extends FilterInputStream implements Pipeline<InputStream, IOException> {
	@NonNull
	private final ThrowingRunnable<? extends IOException> closeable;
	private final AtomicBoolean closed = new AtomicBoolean();

	public InputStreamPipeline(Pipeline<? extends InputStream, ? extends IOException> pipeline) throws IOException {
		this(pipeline.get(), pipeline::close);
	}

	public InputStreamPipeline(InputStream in, ThrowingRunnable<? extends IOException> closeable) {
		super(in);
		this.closeable = closeable;
	}

	@Override
	public InputStream get() throws IOException {
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
