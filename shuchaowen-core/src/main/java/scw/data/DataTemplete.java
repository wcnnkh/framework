package scw.data;

import scw.beans.annotation.AutoImpl;
import scw.data.memory.MemoryDataTemplete;

@AutoImpl({ RedisDataTemplete.class, MemcachedDataTemplete.class, MemoryDataTemplete.class })
public interface DataTemplete extends TemporaryCache, TemporaryCounter {
}
