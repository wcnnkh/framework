package scw.net.message;

import java.nio.charset.Charset;

import scw.core.Constants;
import scw.core.utils.StringUtils;
import scw.http.HttpHeaders;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;

public abstract class AbstractMessage implements Message {
	public long getContentLength() {
		String len = getHeaders().getFirst(HttpHeaders.CONTENT_LENGTH);
		return StringUtils.parseLong(len, -1);
	}

	public MimeType getContentType() {
		String contentType = getRawContentType();
		return StringUtils.hasText(contentType) ? MimeTypeUtils.parseMimeType(contentType) : null;
	}

	public String getRawContentType() {
		return getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
	}

	public Charset getCharset() {
		MimeType mimeType = getContentType();
		if (mimeType == null) {
			return getDefaultCharset();
		}

		Charset charset = mimeType.getCharset();
		return charset == null ? getDefaultCharset() : charset;
	}

	protected Charset getDefaultCharset() {
		return Constants.DEFAULT_CHARSET;
	}
}
