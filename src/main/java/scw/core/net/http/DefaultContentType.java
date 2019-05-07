package scw.core.net.http;

import java.io.Serializable;
import java.nio.charset.Charset;

import scw.core.KeyValuePair;

public class DefaultContentType implements ContentType, Serializable {
	private static final long serialVersionUID = 1L;
	private final String mimeType;
	private final Charset charset;
	private final KeyValuePair<String, String>[] params;

	DefaultContentType() {
		this.mimeType = null;
		this.charset = null;
		this.params = null;
	};

	public DefaultContentType(String mimeType) {
		this.mimeType = mimeType;
		this.charset = null;
		this.params = null;
	}

	public DefaultContentType(String mimeType, Charset charset) {
		this(mimeType, charset, null);
	}

	public DefaultContentType(String mimeType, String charsetName) {
		this(mimeType, charsetName, null);
	}

	public DefaultContentType(String mimeType, String charsetName, KeyValuePair<String, String>[] params) {
		this(mimeType, Charset.forName(charsetName), params);
	}

	public DefaultContentType(String mimeType, Charset charset, KeyValuePair<String, String>[] params) {
		this.mimeType = mimeType;
		this.charset = charset;
		this.params = params;
	}

	public String getMimeType() {
		return mimeType;
	}

	public Charset getCharset() {
		return charset;
	}

	public KeyValuePair<String, String>[] getParams() {
		return params;
	}

	public String asString() {
		StringBuilder sb = new StringBuilder();
		sb.append(mimeType);
		if (charset != null) {
			sb.append("; ").append("charset=").append(charset.name());
		}

		if (params != null && params.length != 0) {
			for (KeyValuePair<String, String> pair : params) {
				sb.append("; ").append(pair.getKey()).append("=").append(pair.getValue());
			}
		}
		return sb.toString();
	}

}
