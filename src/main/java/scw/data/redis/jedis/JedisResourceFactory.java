package scw.data.redis.jedis;

import redis.clients.jedis.Jedis;
import scw.beans.annotation.AutoImpl;
import scw.core.ResourceFactory;

@AutoImpl({ JedisPoolResourceFactory.class })
public interface JedisResourceFactory extends ResourceFactory<Jedis> {
}
