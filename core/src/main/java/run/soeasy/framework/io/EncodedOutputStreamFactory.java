package run.soeasy.framework.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.domain.CharsetCapable;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

@Data
class EncodedOutputStreamFactory<T extends OutputStream, R extends Writer, W extends OutputStreamFactory<T>>
		implements OutputStreamFactory<T>, CharsetCapable {
	@NonNull
	private final W source;
	private final Object charset;
	@NonNull
	private final ThrowingFunction<? super T, ? extends R, IOException> encoder;

	@Override
	public @NonNull Pipeline<T, IOException> getOutputStreamPipeline() {
		return source.getOutputStreamPipeline();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return source.getOutputStream();
	}

	@Override
	public @NonNull Pipeline<Writer, IOException> getWriterPipeline() {
		return getOutputStreamPipeline().map((e) -> (Writer) encoder.apply(e)).onClose((e) -> e.close());
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
