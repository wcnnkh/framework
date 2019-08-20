package scw.net.support;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URLConnection;

import scw.io.IOUtils;
import scw.net.DecoderFilter;
import scw.net.DecoderFilterChain;

public abstract class AbstractTextDecoderFilter implements DecoderFilter {
	private String charsetName;

	public AbstractTextDecoderFilter(String charsetName) {
		this.charsetName = charsetName;
	}
	
	public Object decode(URLConnection urlConnection, Type type, DecoderFilterChain chain) throws Exception {
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

	public final String getCharsetName(){
		return charsetName;
	}
	
	protected abstract boolean isVerifyType(Type type);

	protected abstract Object textDecoder(String contentType, String text, Type type);
}
