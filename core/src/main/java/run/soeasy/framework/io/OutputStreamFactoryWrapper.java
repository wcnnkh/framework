package run.soeasy.framework.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

@FunctionalInterface
public interface OutputStreamFactoryWrapper<O extends OutputStream, W extends OutputStreamFactory<O>>
		extends OutputStreamFactory<O>, WriterFactoryWrapper<Writer, W> {
	@Override
	default Pipeline<O, IOException> getOutputStreamPipeline() {
		return getSource().getOutputStreamPipeline();
	}

	@Override
	default OutputStream getOutputStream() throws IOException {
		return getSource().getOutputStream();
	}

	@Override
	default @NonNull Pipeline<Writer, IOException> getWriterPipeline() {
		return getSource().getWriterPipeline();
	}

	@Override
	default <T extends Writer> OutputStreamFactory<O> encode(
			@NonNull ThrowingFunction<? super O, ? extends T, IOException> encoder) {
		return getSource().encode(encoder);
	}

	@Override
	default OutputStreamFactory<O> encode(Charset charset) {
		return getSource().encode(charset);
	}

	@Override
	default OutputStreamFactory<O> encode(String charsetName) {
		return getSource().encode(charsetName);
	}

	@Override
	default boolean isEncoded() {
		return getSource().isEncoded();
	}

	@Override
	default WritableByteChannel writableChannel() throws IOException {
		return getSource().writableChannel();
	}
}