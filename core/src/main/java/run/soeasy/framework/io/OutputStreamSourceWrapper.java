package run.soeasy.framework.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.WritableByteChannel;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;

public interface OutputStreamSourceWrapper<T extends OutputStream, W extends OutputStreamSource<T>>
		extends OutputStreamSource<T>, OutputStreamFactoryWrapper<T, W> {
	@Override
	default T getOutputStream() throws IOException {
		return getSource().getOutputStream();
	}

	@Override
	default @NonNull Pipeline<T, IOException> getOutputStreamPipeline() {
		return getSource().getOutputStreamPipeline();
	}

	@Override
	default WritableByteChannel writableChannel() throws IOException {
		return getSource().writableChannel();
	}
}
