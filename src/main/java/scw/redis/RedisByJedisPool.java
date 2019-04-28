package scw.redis;

import scw.beans.annotation.Bean;

/**
 * 此类已弃用
 * 
 * @author shuchaowen
 *
 */
@Bean(proxy = false)
public class RedisByJedisPool extends scw.data.redis.jedis.RedisByJedisPool implements Redis {

	public RedisByJedisPool(String propertiesFile) {
		super(propertiesFile);
	}

	public RedisByJedisPool() {
		super();
	}

	public RedisByJedisPool(int maxTotal, int maxIdle, boolean testOnBorrow, String host) {
		super(maxTotal, maxIdle, testOnBorrow, host);
	}
}
