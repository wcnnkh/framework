package run.soeasy.framework.io.pipeline;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;

@FunctionalInterface
public interface InputStreamSource<T extends InputStream> extends InputStreamFactory<T> {
	T getInputStream() throws IOException;

	@Override
	default @NonNull Pipeline<T, IOException> getInputStreamPipeline() {
		return Pipeline.forCloseable(this::getInputStream);
	}

	default ReadableByteChannel readableChannel() throws IOException {
		return Channels.newChannel(getInputStream());
	}
}
