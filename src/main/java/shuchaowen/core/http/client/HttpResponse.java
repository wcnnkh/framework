package shuchaowen.core.http.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;

import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.http.client.decoder.Decoder;
import shuchaowen.core.http.client.decoder.StringDecoder;

public class HttpResponse implements Response {
	private HttpURLConnection httpURLConnection;

	public HttpResponse(HttpURLConnection httpURLConnection) {
		this.httpURLConnection = httpURLConnection;
	}

	public StatusLine getStatusLine() {
		return null;
	}

	public void disconnect() {
		try {
			httpURLConnection.getInputStream().close();
		} catch (IOException e) {
			throw new ShuChaoWenRuntimeException(e);
		}
		httpURLConnection.disconnect();
	}

	public String getContentType() {
		return httpURLConnection.getContentType();
	}

	@Override
	public String toString() {
		StringDecoder decoder = new StringDecoder();
		try {
			return decoder.decode(httpURLConnection.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public <T> T decode(Decoder<T> decoder){
		try {
			return decoder.decode(httpURLConnection.getInputStream());
		} catch (IOException e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}

	public InputStream getInputStream() {
		try {
			return httpURLConnection.getInputStream();
		} catch (IOException e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}

	public URLConnection getURLConnection() {
		return httpURLConnection;
	}
}
