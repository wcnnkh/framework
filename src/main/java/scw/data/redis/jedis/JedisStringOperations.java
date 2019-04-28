package scw.data.redis.jedis;

import redis.clients.jedis.Jedis;
import scw.data.redis.ResourceManager;

public class JedisStringOperations extends AbstractJedisStringOperations {
	private final ResourceManager<Jedis> resourceManager;

	public JedisStringOperations(ResourceManager<Jedis> resourceManager) {
		this.resourceManager = resourceManager;
	}

	public Jedis getResource() {
		return resourceManager.getResource();
	}

	public void close(Jedis resource) {
		resourceManager.close(resource);
	}
}
