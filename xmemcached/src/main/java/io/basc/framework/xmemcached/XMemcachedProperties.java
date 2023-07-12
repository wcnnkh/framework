package io.basc.framework.xmemcached;

import java.util.ArrayList;
import java.util.List;

import io.basc.framework.memcached.config.MemcachedNodeProperties;
import lombok.Data;
import net.rubyeye.xmemcached.CommandFactory;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;

@Data
public class XMemcachedProperties {
	private List<MemcachedNodeProperties> nodes;

	/**
	 * 宕机报警
	 */
	private boolean failureMode = true;

	/**
	 * 默认使用二进制协议
	 */
	private CommandFactory commandFactory = new BinaryCommandFactory();

	public void addNode(MemcachedNodeProperties node) {
		if (nodes == null) {
			nodes = new ArrayList<>();
		}
		nodes.add(node);
	}
}
