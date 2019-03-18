package scw.net.response;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import scw.common.ByteArray;
import scw.common.utils.IOUtils;
import scw.core.NestedRuntimeException;

public final class Body implements Serializable {
	private static final long serialVersionUID = 1L;
	private final ByteArray byteArray;

	public Body(InputStream is) throws IOException {
		this.byteArray = IOUtils.read(is, 1024, -1);
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

	public String toString() {
		return toString("UTF-8");
	}
}
