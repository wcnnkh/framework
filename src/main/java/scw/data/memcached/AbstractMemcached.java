package scw.data.memcached;

import scw.core.serializer.NoTypeSpecifiedSerializer;

public abstract class AbstractMemcached implements Memcached {
	protected final NoTypeSpecifiedSerializer serializer;

	public AbstractMemcached(NoTypeSpecifiedSerializer serializer) {
		this.serializer = serializer;
	}

}
