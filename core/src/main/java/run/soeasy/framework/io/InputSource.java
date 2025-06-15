package run.soeasy.framework.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

@FunctionalInterface
public interface InputSource extends InputStreamFactory<InputStream> {
	@Override
	InputStream getInputStream() throws IOException;

	@Override
	default @NonNull Pipeline<InputStream, IOException> getInputStreamPipeline() {
		return Pipeline.forCloseable(this::getInputStream);
	}

	@Override
	default InputSource decode(@NonNull Charset charset) {
		return new DecodedInputSource<>(this, charset, (e) -> new InputStreamReader(e, charset));
	}

	@Override
	default InputSource decode(@NonNull String charsetName) {
		return new DecodedInputSource<>(this, charsetName, (e) -> new InputStreamReader(e, charsetName));
	}

	@Override
	default <T extends Reader> InputSource decode(
			@NonNull ThrowingFunction<? super InputStream, ? extends T, IOException> decoder) {
		return new DecodedInputSource<>(this, null, decoder);
	}
}
