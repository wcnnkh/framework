package scw.net;

import java.net.URLConnection;
import java.util.Collection;

public final class DecoderResponse implements Response<Object> {
	private final Class<?> type;
	private final DecoderFilterChain filterChain;

	public DecoderResponse(Class<?> type, Collection<DecoderFilter> decoderFilters) {
		this.type = type;
		this.filterChain = new DefaultDecoderFilterChain(decoderFilters);
	}

	public DecoderResponse(Class<?> type, DecoderFilterChain filterChain) {
		this.type = type;
		this.filterChain = filterChain;
	}

	public Object response(URLConnection urlConnection) throws Throwable {
		return filterChain.doDecode(urlConnection, type);
	}

}
