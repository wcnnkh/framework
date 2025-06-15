package run.soeasy.framework.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class CloseableOutputStreamWriter extends OutputStreamWriter {
	private final OutputStream out;

	public CloseableOutputStreamWriter(OutputStream out) {
		super(out);
		this.out = out;
	}

	public CloseableOutputStreamWriter(OutputStream out, Charset cs) {
		super(out, cs);
		this.out = out;
	}

	public CloseableOutputStreamWriter(OutputStream out, CharsetEncoder enc) {
		super(out, enc);
		this.out = out;
	}

	public CloseableOutputStreamWriter(OutputStream out, String charsetName) throws UnsupportedEncodingException {
		super(out, charsetName);
		this.out = out;
	}

	@Override
	public void flush() throws IOException {
		try {
			super.flush();
		} finally {
			out.flush();
		}
	}

	@Override
	public void close() throws IOException {
		try {
			super.close();
		} finally {
			out.close();
		}
	}
}
