package scw.net.message;

import scw.net.MimeType;

public abstract class AbstractOutputMessage extends AbstractMessage implements OutputMessage {

	public void setContentType(MimeType contentType) {
		getHeaders().set(getContentTypeHeaderName(), contentType.toString());
	}

	public void setContentLength(long contentLength) {
		getHeaders().set(getContentLengthHeaderName(), contentLength + "");
	}
}
