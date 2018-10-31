package shuchaowen.core.db.storage.cache;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.storage.async.AsyncConsumer;
import shuchaowen.core.db.storage.async.DefaultAsyncConsumer;
import shuchaowen.core.db.storage.async.MemcachedAsyncStorage;
import shuchaowen.memcached.Memcached;

public class MemcachedCacheStorage extends CacheStorage {
	public MemcachedCacheStorage(AbstractDB db, Memcached memcached) {
		super(new MemcachedCache(memcached), new MemcachedAsyncStorage(db, memcached, db.getClass().getName(), new DefaultAsyncConsumer()));
	}
	
	public MemcachedCacheStorage(AbstractDB db, Memcached memcached, AsyncConsumer asyncConsumer) {
		super(new MemcachedCache(memcached), new MemcachedAsyncStorage(db, memcached, db.getClass().getName(), asyncConsumer));
	}

	public MemcachedAsyncStorage getMemcachedAsyncStorage() {
		return (MemcachedAsyncStorage) getAsyncStroage();
	}

	public Memcached getMemcached() {
		return getMemcachedAsyncStorage().getMemcached();
	}
}
