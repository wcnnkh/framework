package io.basc.framework.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import io.basc.framework.cloud.Service;
import io.basc.framework.cloud.SimpleDiscoveryClient;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.io.serializer.JavaSerializer;
import io.basc.framework.util.logging.LogManager;

public class ZooKeeperServiceDiscovery extends SimpleDiscoveryClient<Service> implements Watcher {
	private static Logger logger = LogManager.getLogger(ZooKeeperServiceDiscovery.class);
	private final ZooKeeper zooKeeper;
	private final String parentPath;

	public ZooKeeperServiceDiscovery(ZooKeeper zooKeeper, String parentPath) {
		this.zooKeeper = zooKeeper;
		this.parentPath = ZooKeeperUtils.cleanPath(parentPath);
		zooKeeper.register(this);
	}

	private Service getInstanceInfo(String path) {
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

		Service instance = getInstanceInfo(eventPath);
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

	public void register(Service instance) {
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

	public void deregister(Service instance) {
		String path = ZooKeeperUtils.cleanPath(parentPath, instance.getName(), instance.getId());
		ZooKeeperUtils.delete(zooKeeper, path, -1);
		super.deregister(instance);
	}
}
