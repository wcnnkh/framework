package io.basc.framework.memcached.config;

import io.basc.framework.util.select.Weighted;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * memcached节点
 * 
 * @author shuchaowen
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class MemcachedNodeProperties extends MemcachedProperties implements Weighted {

	/**
	 * 集群模式的权重
	 */
	private int weight = 1;

	public MemcachedNodeProperties(String host) {
		super(host);
	}
}
