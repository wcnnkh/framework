package scw.net;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLConnection;

import scw.core.utils.StringUtils;
import scw.net.mime.MimeType;
import scw.net.mime.MimeTypeUtils;

public abstract class URLConnectionClientRequest implements ClientRequest {
	static final int DEFAULT_CONNECT_TIMEOUT = 10000;
	static final int DEFAULT_READ_TIMEOUT = 10000;

	public abstract URLConnection getUrlConnection();

	public void setReadTimeout(int timeout) {
		getUrlConnection().setReadTimeout(timeout);
	}

	public int getReadTimeout() {
		return getUrlConnection().getReadTimeout() <= 0 ? DEFAULT_READ_TIMEOUT : getUrlConnection().getReadTimeout();
	}

	public void setConnectTimeout(int timeout) {
		getUrlConnection().setConnectTimeout(timeout);
	}

	public int getConnectTimeout() {
		return getUrlConnection().getConnectTimeout() <= 0 ? DEFAULT_CONNECT_TIMEOUT
				: getUrlConnection().getConnectTimeout();
	}

	public void setContentType(MimeType contentType) {
		getUrlConnection().setRequestProperty("Content-Type", contentType.toString());
	}

	public void setContentLength(long contentLength) {
		getUrlConnection().setRequestProperty("Content-Length", contentLength + "");
	}

	public OutputStream getBody() throws IOException {
		return getUrlConnection().getOutputStream();
	}

	public MimeType getContentType() {
		String contentType = getUrlConnection().getContentType();
		return StringUtils.hasLength(contentType) ? MimeTypeUtils.parseMimeType(contentType) : null;
	}

	public long getContentLength() {
		return getUrlConnection().getContentLength();
	}
}
