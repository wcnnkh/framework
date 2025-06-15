package run.soeasy.framework.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class CloseableInputStreamReader extends InputStreamReader {
	private final InputStream in;

	public CloseableInputStreamReader(InputStream in, String charsetName) throws UnsupportedEncodingException {
		super(in, charsetName);
		this.in = in;
	}

	public CloseableInputStreamReader(InputStream in, CharsetDecoder dec) {
		super(in, dec);
		this.in = in;
	}

	public CloseableInputStreamReader(InputStream in, Charset cs) {
		super(in, cs);
		this.in = in;
	}

	public CloseableInputStreamReader(InputStream in) {
		super(in);
		this.in = in;
	}

	@Override
	public void close() throws IOException {
		try {
			super.close();
		} finally {
			in.close();
		}
	}
}
