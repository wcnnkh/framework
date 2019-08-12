package scw.net.support;

import java.io.InputStream;
import java.net.URLConnection;

import scw.io.IOUtils;
import scw.net.DecoderFilter;
import scw.net.DecoderFilterChain;

public abstract class AbstractTextDecoderFilter implements DecoderFilter {

	public Object decode(URLConnection urlConnection, Class<?> type, DecoderFilterChain chain) throws Exception {
		if (!urlConnection.getDoInput() || type == Void.class) {
			return null;
		}

		if (isVerifyType(type)) {
			String charsetName = urlConnection.getContentEncoding();
			if (charsetName == null) {
				charsetName = getCharsetName();
			}

			InputStream inputStream = null;
			try {
				inputStream = urlConnection.getInputStream();
				return textDecoder(urlConnection.getContentType(), IOUtils.readContent(inputStream, charsetName), type);
			} finally {
				IOUtils.close(inputStream);
			}
		}
		return chain.doDecode(urlConnection, type);
	}

	public abstract String getCharsetName();

	protected abstract boolean isVerifyType(Class<?> type);

	protected abstract Object textDecoder(String contentType, String text, Class<?> type);
}
