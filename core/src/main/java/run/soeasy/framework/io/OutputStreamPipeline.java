package run.soeasy.framework.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingRunnable;

class OutputStreamPipeline extends FilterOutputStream implements Pipeline<OutputStream, IOException> {
	@NonNull
	private final ThrowingRunnable<? extends IOException> closeable;
	private final AtomicBoolean closed = new AtomicBoolean();

	public OutputStreamPipeline(Pipeline<? extends OutputStream, ? extends IOException> pipeline) throws IOException {
		this(pipeline.get(), pipeline::close);
	}

	public OutputStreamPipeline(OutputStream out, ThrowingRunnable<? extends IOException> closeable) {
		super(out);
		this.closeable = closeable;
	}

	@Override
	public OutputStream get() throws IOException {
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
