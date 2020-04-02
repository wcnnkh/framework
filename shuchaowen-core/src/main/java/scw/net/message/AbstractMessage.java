package scw.net.message;

import scw.core.utils.StringUtils;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;
import scw.net.http.HttpHeaders;

public abstract class AbstractMessage implements Message {
	protected String getContentLengthHeaderName() {
		return HttpHeaders.CONTENT_LENGTH;
	}

	protected String getContentTypeHeaderName() {
		return HttpHeaders.CONTENT_TYPE;
	}

	public long getContentLength() {
		String len = getHeaders().getFirst(getContentLengthHeaderName());
		return StringUtils.hasLength(len) ? StringUtils.parseLong(len) : -1;
	}

	public MimeType getContentType() {
		String contentType = getRawContentType();
		return StringUtils.hasLength(contentType) ? MimeTypeUtils.parseMimeType(contentType) : null;
	}

	public String getRawContentType() {
		return getHeaders().getFirst(getContentTypeHeaderName());
	}
}
