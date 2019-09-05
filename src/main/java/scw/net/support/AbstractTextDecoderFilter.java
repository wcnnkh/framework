package scw.net.support;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URLConnection;

import scw.core.utils.StringParse;
import scw.io.IOUtils;
import scw.net.DecoderFilter;
import scw.net.DecoderFilterChain;

public abstract class AbstractTextDecoderFilter extends StringParse implements DecoderFilter {
	private String charsetName;

	public AbstractTextDecoderFilter(String charsetName) {
		this.charsetName = charsetName;
	}

	public Object decode(URLConnection urlConnection, Type type, DecoderFilterChain chain) throws Throwable {
		if (!urlConnection.getDoInput() || type == Void.class) {
			return null;
		}

		if (isVerifyType(type, urlConnection)) {
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

	public final String getCharsetName() {
		return charsetName;
	}

	protected abstract boolean isVerifyType(Type type, URLConnection urlConnection);

	protected abstract Object textDecoder(String contentType, String text, Type type);
}
