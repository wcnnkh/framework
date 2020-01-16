package scw.net.message;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import scw.core.utils.StringUtils;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;

public abstract class AbstractMessage implements Message {
	public String getHeader(String name) {
		return getHeaders().getFirst(name);
	}

	@SuppressWarnings("unchecked")
	public Enumeration<String> getHeaders(String name) {
		List<String> values = getHeaders().get(name);
		return (Enumeration<String>) (values == null ? Collections.emptyEnumeration()
				: Collections.enumeration(values));
	}

	@SuppressWarnings("unchecked")
	public Enumeration<String> getHeaderNames() {
		Set<String> keys = getHeaders().keySet();
		return (Enumeration<String>) (keys == null ? Collections.emptyEnumeration() : Collections.enumeration(keys));
	}

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
		String contentType = getRawContentType();
		return StringUtils.hasLength(contentType) ? MimeTypeUtils.parseMimeType(contentType) : null;
	}

	public String getRawContentType() {
		return getHeaders().getFirst(getContentTypeHeaderName());
	}
}
