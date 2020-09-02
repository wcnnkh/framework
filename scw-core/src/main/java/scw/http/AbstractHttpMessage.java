package scw.http;

public abstract class AbstractHttpMessage implements HttpMessage {
	public long getContentLength() {
		return getHeaders().getContentLength();
	}

	public MediaType getContentType() {
		return getHeaders().getContentType();
	}

	public String getCharacterEncoding() {
		MediaType mediaType = getContentType();
		return mediaType == null ? null : mediaType.getCharsetName();
	}
}
