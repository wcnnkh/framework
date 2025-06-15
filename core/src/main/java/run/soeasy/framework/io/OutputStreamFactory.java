package run.soeasy.framework.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

@FunctionalInterface
public interface OutputStreamFactory<O extends OutputStream> extends WriterFactory<Writer> {

	@NonNull
	Pipeline<O, IOException> getOutputStreamPipeline();

	default OutputStream getOutputStream() throws IOException {
		return new OutputStreamPipeline(getOutputStreamPipeline());
	}

	@Override
	default @NonNull Pipeline<Writer, IOException> getWriterPipeline() {
		return getOutputStreamPipeline().map((e) -> (Writer) new OutputStreamWriter(e)).onClose((e) -> e.close());
	}

	default boolean isEncoded() {
		return false;
	}

	default <T extends Writer> OutputStreamFactory<O> encode(
			@NonNull ThrowingFunction<? super O, ? extends T, IOException> encoder) {
		return new EncodedOutputStreamFactory<>(this, null, encoder);
	}

	default OutputStreamFactory<O> encode(Charset charset) {
		return new EncodedOutputStreamFactory<>(this, charset, (e) -> new OutputStreamWriter(e, charset));
	}

	default OutputStreamFactory<O> encode(String charsetName) {
		return new EncodedOutputStreamFactory<>(this, charsetName, (e) -> new OutputStreamWriter(e, charsetName));
	}

	default WritableByteChannel writableChannel() throws IOException {
		return Channels.newChannel(getOutputStream());
	}
}
