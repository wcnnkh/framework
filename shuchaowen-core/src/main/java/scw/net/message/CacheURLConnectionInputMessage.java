package scw.net.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URLConnection;

import scw.io.IOUtils;
import scw.io.UnsafeByteArrayInputStream;
import scw.io.UnsafeByteArrayOutputStream;
import scw.net.mime.MimeType;
import scw.net.mime.MimeTypeUtils;
import scw.util.MultiValueMap;
import scw.util.MultiValueMapWrapper;

public class CacheURLConnectionInputMessage extends AbstractInputMessage
		implements Serializable {
	private static final long serialVersionUID = 1L;
	private byte[] data;
	private final String defaultCharsetName;
	private final MimeType contentType;
	private final MultiValueMap<String, String> headers;

	public CacheURLConnectionInputMessage(URLConnection urlConnection)
			throws IOException {
		this.headers = new MultiValueMapWrapper<String, String>(
				urlConnection.getHeaderFields());
		this.contentType = MimeTypeUtils.parseMimeType(urlConnection
				.getContentType());
		this.defaultCharsetName = urlConnection.getContentEncoding();

		if (urlConnection.getDoInput()) {
			UnsafeByteArrayOutputStream out = new UnsafeByteArrayOutputStream(
					Math.max(urlConnection.getContentLength(), 1024));
			InputStream in = null;
			try {
				in = urlConnection.getInputStream();
				IOUtils.write(in, out, 2048);
				data = out.toByteArray();
			} finally {
				IOUtils.close(out, in);
			}
		}
	}

	public MultiValueMap<String, String> getHeaders() {
		return headers;
	}

	public final InputStream getBody() {
		return new UnsafeByteArrayInputStream(data);
	}

	public final long getContentLength() {
		return data.length;
	}

	public byte[] toByteArray() {
		return data == null ? null : data.clone();
	}

	public MimeType getContentType() {
		return contentType;
	}

	@Override
	protected String getDefaultCharsetName() {
		return defaultCharsetName;
	}
}
