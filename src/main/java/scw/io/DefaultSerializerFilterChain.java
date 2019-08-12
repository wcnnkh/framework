package scw.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;

import scw.core.exception.NotSupportException;
import scw.core.utils.CollectionUtils;

public class DefaultSerializerFilterChain implements SerializerFilterChain {
	private Iterator<SerializerFilter> iterator;

	public DefaultSerializerFilterChain(Collection<SerializerFilter> serializerFilters) {
		if (!CollectionUtils.isEmpty(serializerFilters)) {
			iterator = serializerFilters.iterator();
		}
	}

	public void doSerialize(Class<?> type, Object data, OutputStream output) throws IOException {
		if (iterator == null) {
			throw new NotSupportException("不支持的编码器：" + type);
		}

		if (iterator.hasNext()) {
			iterator.next().serialize(type, data, output, this);
			return;
		}

		throw new NotSupportException("不支持的编码器：" + type);
	}

}
