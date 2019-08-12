package scw.net;

import java.net.URLConnection;
import java.util.Collection;
import java.util.Iterator;

import scw.core.exception.NotSupportException;
import scw.core.utils.CollectionUtils;

public class DefaultDecoderFilterChain implements DecoderFilterChain {
	private Iterator<DecoderFilter> iterator;

	public DefaultDecoderFilterChain(Collection<DecoderFilter> decoderFilters) {
		if (!CollectionUtils.isEmpty(decoderFilters)) {
			this.iterator = decoderFilters.iterator();
		}
	}

	public Object doDecode(URLConnection urlConnection, Class<?> type) throws Exception {
		if (iterator == null) {
			throw new NotSupportException("不支持是解码：" + type);
		}

		if (iterator.hasNext()) {
			return iterator.next().decode(urlConnection, type, this);
		}

		throw new NotSupportException("不支持的解码方式：" + type);
	}

}
