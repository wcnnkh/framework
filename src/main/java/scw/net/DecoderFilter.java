package scw.net;

import java.net.URLConnection;

public interface DecoderFilter {
	Object decode(URLConnection urlConnection, Class<?> type, DecoderFilterChain chain) throws Exception;
}
