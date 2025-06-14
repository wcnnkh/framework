package run.soeasy.framework.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;

public interface InputStreamSourceWrapper<T extends InputStream, W extends InputStreamSource<T>>
		extends InputStreamSource<T>, InputStreamFactoryWrapper<T, W> {
	@Override
	default T getInputStream() throws IOException {
		return getSource().getInputStream();
	}

	@Override
	default @NonNull Pipeline<T, IOException> getInputStreamPipeline() {
		return getSource().getInputStreamPipeline();
	}

	@Override
	default ReadableByteChannel readableChannel() throws IOException {
		return getSource().readableChannel();
	}
}