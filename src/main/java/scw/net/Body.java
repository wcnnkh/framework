package scw.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import scw.common.ByteArray;
import scw.common.exception.NestedRuntimeException;
import scw.common.utils.IOUtils;

public final class Body implements Serializable {
	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	private static final long serialVersionUID = 1L;
	private final ByteArray byteArray;

	public Body(InputStream is) throws IOException {
		this.byteArray = IOUtils.read(is, 1024, -1);
	}

	public Body(String body, String charsetName) {
		try {
			this.byteArray = new ByteArray(body.getBytes(charsetName));
		} catch (UnsupportedEncodingException e) {
			throw new NestedRuntimeException(e);
		}
	}

	public Body(String body, Charset charset) {
		this.byteArray = new ByteArray(body.getBytes(charset));
	}

	public Body(String body) {
		this.byteArray = new ByteArray(body.getBytes(DEFAULT_CHARSET));
	}

	public ByteArray getByteArray() {
		return byteArray;
	}

	public String toString(Charset charset) {
		return new String(byteArray.toByteArray(), charset);
	}

	public String toString(String charsetName) {
		try {
			return new String(byteArray.toByteArray(), charsetName);
		} catch (UnsupportedEncodingException e) {
			throw new NestedRuntimeException(e);
		}
	}

	public void writeTo(OutputStream os) throws IOException {
		if (byteArray != null) {
			byteArray.writeTo(os);
		}
	}

	public String toString() {
		return toString(DEFAULT_CHARSET);
	}
}
