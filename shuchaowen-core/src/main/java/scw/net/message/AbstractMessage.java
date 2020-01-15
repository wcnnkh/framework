package scw.net.message;

import scw.core.utils.StringUtils;
import scw.net.mime.MimeType;
import scw.net.mime.MimeTypeUtils;

public abstract class AbstractMessage implements Message {

	protected String getContentLengthHeaderName() {
		return "Content-Length";
	}

	protected String getContentTypeHeaderName() {
		return "Content-Type";
	}

	public long getContentLength() {
		String len = getHeaders().getFirst(getContentLengthHeaderName());
		return StringUtils.hasLength(len) ? StringUtils.parseLong(len) : -1;
	}

	public MimeType getContentType() {
		String contentType = getHeaders().getFirst(getContentTypeHeaderName());
		return StringUtils.hasLength(contentType) ? MimeTypeUtils.parseMimeType(contentType) : null;
	}
}
