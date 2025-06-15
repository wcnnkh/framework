package run.soeasy.framework.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

public interface OutputSource extends OutputStreamFactory<OutputStream> {
	@Override
	OutputStream getOutputStream() throws IOException;

	@Override
	default @NonNull Pipeline<OutputStream, IOException> getOutputStreamPipeline() {
		return Pipeline.forCloseable(this::getOutputStream);
	}

	@Override
	default OutputSource encode(Charset charset) {
		return new EncodedOutputSource<>(this, charset, (e) -> new OutputStreamWriter(e, charset));
	}

	@Override
	default OutputSource encode(String charsetName) {
		return new EncodedOutputSource<>(this, charsetName, (e) -> new OutputStreamWriter(e, charsetName));
	}

	@Override
	default <T extends Writer> OutputSource encode(
			@NonNull ThrowingFunction<? super OutputStream, ? extends T, IOException> encoder) {
		return new EncodedOutputSource<>(this, null, encoder);
	}
}
