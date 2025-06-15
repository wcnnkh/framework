package run.soeasy.framework.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

@FunctionalInterface
public interface OutputSourceWrapper<W extends OutputSource>
		extends OutputSource, OutputStreamFactoryWrapper<OutputStream, W> {
	@Override
	default OutputStream getOutputStream() throws IOException {
		return getSource().getOutputStream();
	}

	@Override
	default @NonNull Pipeline<OutputStream, IOException> getOutputStreamPipeline() {
		return getSource().getOutputStreamPipeline();
	}

	@Override
	default OutputSource encode(Charset charset) {
		return getSource().encode(charset);
	}

	@Override
	default OutputSource encode(String charsetName) {
		return getSource().encode(charsetName);
	}

	@Override
	default <T extends Writer> OutputSource encode(
			@NonNull ThrowingFunction<? super OutputStream, ? extends T, IOException> encoder) {
		return getSource().encode(encoder);
	}
}