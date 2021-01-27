package scw.redis.jedis.cluster;

import redis.clients.jedis.JedisCluster;
import scw.util.ResourcePool;

public interface JedisClusterResourceFactory extends ResourcePool<JedisCluster>{
}
