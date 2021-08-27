package io.basc.framework.zookeeper;

import io.basc.framework.cloud.ServiceInstance;
import io.basc.framework.cloud.SimpleDiscoveryClient;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.io.JavaSerializer;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

@Provider(order=Integer.MIN_VALUE + 1)
public class ZooKeeperServiceDiscovery extends SimpleDiscoveryClient<ServiceInstance> implements Watcher {
	private static Logger logger = LoggerFactory.getLogger(ZooKeeperServiceDiscovery.class);
	public static final String DEFAULT_PARENT_PATH = "/scw";
	private final ZooKeeper zooKeeper;
	private final String parentPath;

	public ZooKeeperServiceDiscovery(ZooKeeper zooKeeper) {
		this(zooKeeper, DEFAULT_PARENT_PATH);
	}

	public ZooKeeperServiceDiscovery(ZooKeeper zooKeeper, String parentPath) {
		this.zooKeeper = zooKeeper;
		this.parentPath = ZooKeeperUtils.cleanPath(parentPath);
		zooKeeper.register(this);
	}

	private ServiceInstance getInstanceInfo(String path) {
		byte[] data = ZooKeeperUtils.getData(zooKeeper, path);
		if (data != null && data.length != 0) {
			try {
				return JavaSerializer.INSTANCE.deserialize(data);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	public void process(WatchedEvent event) {
		if (logger.isTraceEnabled()) {
			logger.trace(event.toString());
		}

		String eventPath = event.getPath();
		if (eventPath == null || !eventPath.startsWith(parentPath)) {
			return;
		}

		ServiceInstance instance = getInstanceInfo(eventPath);
		switch (event.getType()) {
		case NodeDeleted:
			super.deregister(instance);
			break;
		case NodeCreated:
			super.register(instance);
			break;
		default:
			super.register(instance);
			break;
		}

		if (logger.isDebugEnabled()) {
			logger.debug(event.toString());
		}
	}

	public void register(ServiceInstance instance) {
		if (instance.getName().indexOf("/") != -1 || instance.getId().indexOf("/") != -1) {
			throw new IllegalArgumentException("The name or ID cannot have characters '/'");
		}

		byte[] data = JavaSerializer.INSTANCE.serialize(instance);
		String rootPath = ZooKeeperUtils.cleanPath(parentPath, instance.getName());
		ZooKeeperUtils.createNotExist(zooKeeper, rootPath, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		String path = ZooKeeperUtils.cleanPath(rootPath, instance.getId());
		ZooKeeperUtils.createNotExist(zooKeeper, path, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		ZooKeeperUtils.setData(zooKeeper, path, data);
		super.register(instance);
	}

	public void deregister(ServiceInstance instance) {
		String path = ZooKeeperUtils.cleanPath(parentPath, instance.getName(), instance.getId());
		ZooKeeperUtils.delete(zooKeeper, path, -1);
		super.deregister(instance);
	}
}
