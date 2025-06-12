package run.soeasy.framework.io.pipeline;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;

@FunctionalInterface
public interface OutputStreamSource<T extends OutputStream> extends OutputStreamFactory<T> {

	T getOutputStream() throws IOException;

	@Override
	default @NonNull Pipeline<T, IOException> getOutputStreamPipeline() {
		return Pipeline.forCloseable(this::getOutputStream);
	}

	default WritableByteChannel writableChannel() throws IOException {
		return Channels.newChannel(getOutputStream());
	}
}
