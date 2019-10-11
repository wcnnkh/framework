package scw.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.URLConnection;
import java.util.Collection;

import scw.core.Constants;
import scw.core.utils.StringUtils;
import scw.io.IOUtils;
import scw.io.UnsafeByteArrayInputStream;
import scw.io.UnsafeByteArrayOutputStream;
import scw.net.header.SimpleMultiValueHeadersReadOnly;
import scw.net.mime.MimeType;
import scw.net.mime.MimeTypeUtils;

public class URLConnectionMessage extends SimpleMultiValueHeadersReadOnly implements Message, Serializable {
	private static final long serialVersionUID = 1L;
	private final byte[] data;
	private final String contentEncoding;
	private final MimeType mimeType;

	public URLConnectionMessage(URLConnection urlConnection) throws IOException {
		super(urlConnection.getHeaderFields());
		this.mimeType = MimeTypeUtils.parseFirstMimeType(urlConnection.getContentType());
		this.contentEncoding = urlConnection.getContentEncoding();

		UnsafeByteArrayOutputStream out = IOUtils.getUnsafeByteArrayOutputStream();
		InputStream in = null;
		try {
			in = urlConnection.getInputStream();
			IOUtils.write(in, out, 2048);
			data = out.toByteArray();
		} finally {
			IOUtils.close(out, in);
		}
	}

	public final InputStream getInputStream() {
		return new UnsafeByteArrayInputStream(data);
	}

	public final long getContentLength() {
		return data.length;
	}

	public final String getContentEncoding() {
		return contentEncoding;
	}

	@SuppressWarnings("unchecked")
	public <T> T convert(Collection<MessageConverter> messageConverters, Type type) throws Throwable {
		MessageConverterChain chain = new MessageConverterChain(messageConverters);
		return (T) chain.doConvert(this, type);
	}

	public byte[] toByteArray() {
		return data == null ? null : data.clone();
	}

	public String toString(String charsetName) {
		if (data == null) {
			return null;
		}
		return StringUtils.createString(data, charsetName);
	}

	@Override
	public String toString() {
		return toString(Constants.DEFAULT_CHARSET_NAME);
	}

	public MimeType getMimeType() {
		return mimeType;
	}
}
