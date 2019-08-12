package scw.net;

import java.net.URLConnection;

public interface DecoderFilterChain {
	Object doDecode(URLConnection urlConnection, Class<?> type) throws Exception;
}
