package run.soeasy.framework.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import run.soeasy.framework.core.function.ThrowingFunction;

public interface CharsetInputStreamFactory<T extends InputStream, W extends InputStreamFactory<T>>
		extends DecodedInputStreamFactory<T, Reader, W>, CharsetCapable {
	@Override
	default ThrowingFunction<? super T, ? extends Reader, IOException> getDecoder() {
		return (e) -> new InputStreamReader(e, getCharset());
	}
}