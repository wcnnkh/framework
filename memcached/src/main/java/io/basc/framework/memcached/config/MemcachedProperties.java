package io.basc.framework.memcached.config;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * memcached属性
 * 
 * @author shuchaowen
 *
 */
@Data
@NoArgsConstructor
public class MemcachedProperties {
	private String host;
	private int port = 11211;

	/**
	 * 从节点
	 */
	private MemcachedProperties slave;

	public MemcachedProperties(String host) {
		this.host = host;
	}
}
