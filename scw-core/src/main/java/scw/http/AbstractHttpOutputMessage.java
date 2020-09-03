package scw.http;

import scw.net.MimeType;

public abstract class AbstractHttpOutputMessage extends AbstractHttpMessage implements HttpOutputMessage {

	public void setContentType(MediaType contentType) {
		String charsetName = contentType.getCharsetName();
		if (charsetName == null) {
			charsetName = getCharacterEncoding();
			if (charsetName == null) {
				getHeaders().setContentType(contentType);
			} else {
				getHeaders().setContentType(new MediaType(contentType, charsetName));
			}
		} else {
			getHeaders().setContentType(contentType);
		}
	}

	public final void setContentType(MimeType contentType) {
		setContentType(new MediaType(contentType));
	}

	public void setContentLength(long contentLength) {
		getHeaders().setContentLength(contentLength);
	}

	public void setCharacterEncoding(String charsetName) {
		MediaType mediaType = getContentType();
		if (mediaType == null) {
			return;
		}

		setContentType(new MediaType(mediaType, charsetName));
	}
}