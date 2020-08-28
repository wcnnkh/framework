package scw.net.message;

import scw.http.HttpHeaders;
import scw.net.MimeType;

public abstract class AbstractOutputMessage extends AbstractMessage implements OutputMessage {

	public void setContentType(MimeType contentType) {
		getHeaders().set(HttpHeaders.CONTENT_TYPE, contentType.toString());
	}

	public void setContentLength(long contentLength) {
		getHeaders().set(HttpHeaders.CONTENT_LENGTH, contentLength + "");
	}
}
