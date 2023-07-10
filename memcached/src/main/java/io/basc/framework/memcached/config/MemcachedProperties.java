package io.basc.framework.memcached.config;

import io.basc.framework.util.Weighted;
import lombok.Data;

@Data
public class MemcachedProperties implements Weighted {
	private String host;
	private int port = 11211;
	/**
	 * 集群模式的权重
	 */
	private int weight = 1;
}
