package scw.id.db;

import scw.beans.annotation.AutoImpl;

@AutoImpl({MemcachedTableIdFactory.class, RedisTableIdFactory.class})
public interface TableIdFactory {
	long generator(Class<?> tableClass, String fieldName);
}
