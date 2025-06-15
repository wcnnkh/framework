package run.soeasy.framework.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;

public interface InputSource<I extends InputStream, R extends Reader>
		extends InputFactory<I, R>, InputStreamSource<I>, ReaderSource<R> {
	boolean isReadable();

	@Override
	default @NonNull Pipeline<I, IOException> getInputStreamPipeline() {
		return isReadable() ? InputStreamSource.super.getInputStreamPipeline() : Pipeline.empty();
	}

	@Override
	default @NonNull Pipeline<R, IOException> getReaderPipeline() {
		return isReadable() ? ReaderSource.super.getReaderPipeline() : Pipeline.empty();
	}
}
