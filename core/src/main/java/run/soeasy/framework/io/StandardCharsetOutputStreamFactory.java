package run.soeasy.framework.io;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import lombok.NonNull;

public class StandardCharsetOutputStreamFactory<T extends OutputStream, W extends OutputStreamFactory<T>>
		extends StandardEncodedOutputStreamFactory<T, Writer, W> implements CharsetOutputStreamFactory<T, W> {
	private final Object charset;

	public StandardCharsetOutputStreamFactory(@NonNull W source, Charset charset) {
		super(source, (e) -> new OutputStreamWriter(e, charset));
		this.charset = charset;
	}

	public StandardCharsetOutputStreamFactory(@NonNull W source, String charsetName) {
		super(source, (e) -> new OutputStreamWriter(e, charsetName));
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
		return CharsetOutputStreamFactory.super.getCharsetName();
	}

}