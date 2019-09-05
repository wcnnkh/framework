package scw.net;

import java.lang.reflect.Type;
import java.net.URLConnection;

public interface DecoderFilter {
	Object decode(URLConnection urlConnection, Type type, DecoderFilterChain chain) throws Throwable;
}
