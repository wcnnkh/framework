package scw.net;

import java.lang.reflect.Type;
import java.net.URLConnection;
import java.util.Collection;

public final class DecoderResponse implements Response<Object> {
	private final Type type;
	private final DecoderFilterChain filterChain;

	public DecoderResponse(Type type, Collection<DecoderFilter> decoderFilters) {
		this.type = type;
		this.filterChain = new DefaultDecoderFilterChain(decoderFilters);
	}

	public DecoderResponse(Type type, DecoderFilterChain filterChain) {
		this.type = type;
		this.filterChain = filterChain;
	}

	public Object response(URLConnection urlConnection) throws Throwable {
		return filterChain.doDecode(urlConnection, type);
	}

}
