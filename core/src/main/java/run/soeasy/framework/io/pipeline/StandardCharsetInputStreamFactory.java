package run.soeasy.framework.io.pipeline;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import lombok.NonNull;

public class StandardCharsetInputStreamFactory<T extends InputStream, W extends InputStreamFactory<T>>
		extends StandardDecodedInputStreamFactory<T, Reader, W> implements CharsetInputStreamFactory<T, W> {
	private final Object charset;

	public StandardCharsetInputStreamFactory(@NonNull W source, Charset charset) {
		super(source, (e) -> new InputStreamReader(e, charset));
		this.charset = charset;
	}

	public StandardCharsetInputStreamFactory(@NonNull W source, String charsetName) {
		super(source, (e) -> new InputStreamReader(e, charsetName));
		this.charset = charsetName;
	}

	@Override
	public Charset getCharset() {
		if (charset instanceof Charset) {
			return (Charset) charset;
		}
		return Charset.forName(String.valueOf(charset));
	}

	@Override
	public String getCharsetName() {
		if (charset instanceof String) {
			return (String) charset;
		}
		return CharsetInputStreamFactory.super.getCharsetName();
	}

}