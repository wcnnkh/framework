package run.soeasy.framework.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.domain.CharsetCapable;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

@RequiredArgsConstructor
@Getter
class DecodedInputStreamFactory<I extends InputStream, R extends Reader, W extends InputStreamFactory<I>>
		implements InputStreamFactory<I>, CharsetCapable {
	@NonNull
	private final W source;
	private final Object charset;
	@NonNull
	private final ThrowingFunction<? super I, ? extends R, IOException> decoder;

	@Override
	public @NonNull Pipeline<I, IOException> getInputStreamPipeline() {
		return source.getInputStreamPipeline();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return source.getInputStream();
	}

	@Override
	public Pipeline<Reader, IOException> getReaderPipeline() {
		return getInputStreamPipeline().map((e) -> (Reader) decoder.apply(e)).onClose((e) -> e.close());
	}

	@Override
	public Charset getCharset() {
		return CharsetCapable.getCharset(charset);
	}

	@Override
	public String getCharsetName() {
		return CharsetCapable.getCharsetName(charset);
	}
}
