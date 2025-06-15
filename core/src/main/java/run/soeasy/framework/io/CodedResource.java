package run.soeasy.framework.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

@Getter
class CodedResource<W extends Resource> extends EncodedOutputSource<Writer, W> implements ResourceWrapper<W> {
	@NonNull
	private final ThrowingFunction<? super InputStream, ? extends Reader, IOException> decoder;

	public CodedResource(@NonNull W source, Object charset,
			@NonNull ThrowingFunction<? super OutputStream, ? extends Writer, IOException> encoder,
			@NonNull ThrowingFunction<? super InputStream, ? extends Reader, IOException> decoder) {
		super(source, charset, encoder);
		this.decoder = decoder;
	}

	@Override
	public @NonNull Pipeline<Reader, IOException> getReaderPipeline() {
		return getInputStreamPipeline().map((e) -> (Reader) decoder.apply(e)).onClose((e) -> e.close());
	}
}
