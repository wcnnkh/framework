package run.soeasy.framework.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import lombok.NonNull;
import run.soeasy.framework.core.function.ThrowingFunction;

class DecodedInputSource<R extends Reader, W extends InputSource> extends DecodedInputStreamFactory<InputStream, R, W>
		implements InputSource {

	public DecodedInputSource(@NonNull W source, Object charset,
			@NonNull ThrowingFunction<? super InputStream, ? extends R, IOException> decoder) {
		super(source, charset, decoder);
	}
}
