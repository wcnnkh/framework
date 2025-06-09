package run.soeasy.framework.core.io.pipeline;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.CharsetDecoder;

import lombok.NonNull;

public class DefaultDecodedInputStreamFactory<T extends InputStream, W extends InputStreamFactory<T>>
		extends StandardDecodedInputStreamFactory<T, Reader, W> {

	public DefaultDecodedInputStreamFactory(@NonNull W source) {
		super(source, InputStreamReader::new);
	}

	public DefaultDecodedInputStreamFactory(@NonNull W source, @NonNull CharsetDecoder charsetDecoder) {
		super(source, (e) -> new InputStreamReader(e, charsetDecoder));
	}
}
