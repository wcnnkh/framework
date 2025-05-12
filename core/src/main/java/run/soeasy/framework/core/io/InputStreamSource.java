package run.soeasy.framework.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import lombok.NonNull;
import run.soeasy.framework.core.function.stream.Source;

@FunctionalInterface
public interface InputStreamSource<T extends InputStream> extends InputStreamFactory<T> {
	public static interface InputStreamSourceWrapper<T extends InputStream, W extends InputStreamSource<T>>
			extends InputStreamSource<T>, InputStreamFactoryWrapper<T, W> {
		@Override
		default T getInputStream() throws IOException {
			return getSource().getInputStream();
		}

		@Override
		default @NonNull Source<T, IOException> getInputStreamPipeline() {
			return getSource().getInputStreamPipeline();
		}

		@Override
		default ReadableByteChannel readableChannel() throws IOException {
			return getSource().readableChannel();
		}
	}

	T getInputStream() throws IOException;

	@Override
	default @NonNull Source<T, IOException> getInputStreamPipeline() {
		return Source.forCloseable(this::getInputStream);
	}

	default ReadableByteChannel readableChannel() throws IOException {
		return Channels.newChannel(getInputStream());
	}
}
