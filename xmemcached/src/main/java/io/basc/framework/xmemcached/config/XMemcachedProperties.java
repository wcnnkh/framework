package io.basc.framework.xmemcached.config;

import java.util.List;

import io.basc.framework.memcached.config.MemcachedProperties;
import lombok.Data;

@Data
public class XMemcachedProperties {
	private List<MemcachedProperties> servers;
	
	private String addressTemplate;
	
	/**
	 * 宕机报警
	 */
	private boolean failureMode = true;
}
