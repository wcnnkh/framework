package run.soeasy.framework.core.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

import lombok.NonNull;
import run.soeasy.framework.core.exe.Pipeline;

@FunctionalInterface
public interface OutputStreamSource<T extends OutputStream> extends OutputStreamFactory<T> {

	public static interface OutputStreamSourceWrapper<T extends OutputStream, W extends OutputStreamSource<T>>
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

	T getOutputStream() throws IOException;

	@Override
	default @NonNull Pipeline<T, IOException> getOutputStreamPipeline() {
		return Pipeline.forCloseable(this::getOutputStream);
	}

	default WritableByteChannel writableChannel() throws IOException {
		return Channels.newChannel(getOutputStream());
	}
}
