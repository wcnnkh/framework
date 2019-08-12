package scw.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

import scw.core.exception.NotSupportException;
import scw.core.utils.CollectionUtils;

public class DefaultDeserializerFilterChain implements DeserializerFilterChain {
	private Iterator<DeserializerFilter> iterator;

	public DefaultDeserializerFilterChain(Collection<DeserializerFilter> deserializerFilters) {
		if (!CollectionUtils.isEmpty(deserializerFilters)) {
			iterator = deserializerFilters.iterator();
		}
	}

	public Object doDeserialize(Class<?> type, InputStream input) throws IOException {
		if (iterator == null) {
			throw new NotSupportException("不支持的解码：" + type);
		}

		if (iterator.hasNext()) {
			return iterator.next().deserialize(type, input, this);
		}

		throw new NotSupportException("不支持的解码：" + type);
	}

}
