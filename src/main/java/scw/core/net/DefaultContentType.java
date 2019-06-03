package scw.core.net;

import java.io.Serializable;
import java.nio.charset.Charset;

import scw.core.KeyValuePair;

public class DefaultContentType implements ContentType, Serializable {
	private static final long serialVersionUID = 1L;
	private final String mimeType;
	private final String charsetName;
	private final KeyValuePair<String, String>[] params;

	DefaultContentType() {
		this.mimeType = null;
		this.charsetName = null;
		this.params = null;
	};

	public DefaultContentType(String mimeType) {
		this.mimeType = mimeType;
		this.charsetName = null;
		this.params = null;
	}

	public DefaultContentType(String mimeType, Charset charset) {
		this(mimeType, charset.name(), null);
	}

	public DefaultContentType(String mimeType, String charsetName) {
		this(mimeType, charsetName, null);
	}

	public DefaultContentType(String mimeType, String charsetName, KeyValuePair<String, String>[] params) {
		this.mimeType = mimeType;
		this.charsetName = charsetName;
		this.params = params;
	}

	public DefaultContentType(String mimeType, Charset charset, KeyValuePair<String, String>[] params) {
		this.mimeType = mimeType;
		this.charsetName = charset.name();
		this.params = params;
	}

	public String getMimeType() {
		return mimeType;
	}

	public String getCharsetName() {
		return charsetName;
	}

	public KeyValuePair<String, String>[] getParams() {
		return params;
	}

	public String asString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getMimeType());
		if (getCharsetName() != null) {
			sb.append("; ").append("charset=").append(getCharsetName());
		}

		if (getParams() != null) {
			for (KeyValuePair<String, String> pair : getParams()) {
				sb.append("; ").append(pair.getKey()).append("=").append(pair.getValue());
			}
		}
		return sb.toString();
	}

}
