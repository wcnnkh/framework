package run.soeasy.framework.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

@FunctionalInterface
public interface InputSourceWrapper<W extends InputSource>
		extends InputSource, InputStreamFactoryWrapper<InputStream, W> {
	@Override
	default InputStream getInputStream() throws IOException {
		return getSource().getInputStream();
	}

	@Override
	default @NonNull Pipeline<InputStream, IOException> getInputStreamPipeline() {
		return getSource().getInputStreamPipeline();
	}

	@Override
	default InputSource decode(@NonNull Charset charset) {
		return getSource().decode(charset);
	}

	@Override
	default InputSource decode(@NonNull String charsetName) {
		return getSource().decode(charsetName);
	}

	@Override
	default <T extends Reader> InputSource decode(
			@NonNull ThrowingFunction<? super InputStream, ? extends T, IOException> decoder) {
		return getSource().decode(decoder);
	}
}