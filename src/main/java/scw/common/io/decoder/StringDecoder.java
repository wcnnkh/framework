package scw.common.io.decoder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import scw.common.io.Decoder;
import scw.common.io.IOUtils;

public class StringDecoder implements Decoder<String> {
	private final Charset charset;

	public StringDecoder(Charset charset) {
		this.charset = charset;
	}

	public StringDecoder(String charsetName) {
		this(Charset.forName(charsetName));
	}

	public String decode(InputStream in) throws IOException {
		InputStreamReader isr = new InputStreamReader(in, charset);
		return IOUtils.read(isr, 256, 0);
	}
}
