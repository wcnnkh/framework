package scw.net;

import java.lang.reflect.Type;
import java.net.URLConnection;

public interface DecoderFilterChain {
	Object doDecode(URLConnection urlConnection, Type type) throws Exception;
}
