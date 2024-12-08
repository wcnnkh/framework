package io.basc.framework.zookeeper;

import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import io.basc.framework.core.convert.ValueWrapper;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Elements;
import io.basc.framework.util.ListElements;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.actor.ChangeType;
import io.basc.framework.util.actor.broadcast.BroadcastEventRegistry;
import io.basc.framework.util.io.serializer.JavaSerializer;
import io.basc.framework.util.io.serializer.Serializer;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LogManager;
import io.basc.framework.value.AbstractEditablePropertyFactory;

/**
 * 使用zookeeper实现的配置中心
 * 
 * @author wcnnkh
 *
 */
public class ZookeeperCloudPropertyFactory extends AbstractEditablePropertyFactory implements Watcher {
	private static Logger logger = LogManager.getLogger(ZookeeperCloudPropertyFactory.class);
	private final ZooKeeper zooKeeper;
	private final String parentPath;
	private Serializer serializer;

	public ZookeeperCloudPropertyFactory(ZooKeeper zooKeeper, String parentPath) {
		this.zooKeeper = zooKeeper;
		this.parentPath = ZooKeeperUtils.cleanPath(parentPath);
		zooKeeper.register(this);
	}

	public Serializer getSerializer() {
		return serializer == null ? JavaSerializer.INSTANCE : serializer;
	}

	public void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}

	private String getKey(String path) {
		String key = path.substring(parentPath.length());
		if (key.startsWith("/")) {
			key = key.substring(1);
		}
		return key;
	}

	@Override
	public Elements<String> keys() {
		List<String> paths = ZooKeeperUtils.getChildren(zooKeeper, parentPath);
		if (CollectionUtils.isEmpty(paths)) {
			return Elements.empty();
		}

		List<String> list = new ArrayList<String>();
		for (String path : paths) {
			list.add(getKey(path));
		}
		return new ListElements<>(paths);
	}

	public boolean containsKey(String key) {
		if (StringUtils.isEmpty(key)) {
			return false;
		}

		String path = ZooKeeperUtils.cleanPath(parentPath, key);
		return ZooKeeperUtils.isExist(zooKeeper, path);
	}

	public ValueWrapper get(String key) {
		if (StringUtils.isEmpty(key)) {
			return ValueWrapper.EMPTY;
		}

		String path = ZooKeeperUtils.cleanPath(parentPath, key);
		byte[] data = ZooKeeperUtils.getData(zooKeeper, path);
		if (data == null) {
			return ValueWrapper.EMPTY;
		}

		try {
			Object value = getSerializer().deserialize(data);
			return ValueWrapper.of(value);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean remove(String key) {
		if (StringUtils.isEmpty(key)) {
			return false;
		}

		String path = ZooKeeperUtils.cleanPath(parentPath, key);
		return ZooKeeperUtils.delete(zooKeeper, path, -1);
	}

	public void process(WatchedEvent event) {
		if (logger.isTraceEnabled()) {
			logger.trace(event.toString());
		}

		String eventPath = event.getPath();
		if (eventPath == null || !eventPath.startsWith(parentPath)) {
			return;
		}

		String key = getKey(eventPath);
		ChangeType changeType;
		switch (event.getType()) {
		case NodeDeleted:
			changeType = ChangeType.DELETE;
			break;
		case NodeCreated:
			changeType = ChangeType.CREATE;
			break;
		default:
			changeType = ChangeType.UPDATE;
			break;
		}

		try {
			getKeyEventDispatcher().publishEvent(new ChangeEvent<>(changeType, Elements.singleton(key)));
		} catch (Exception e) {
			logger.error(e, "zookeeper config publish error");
		}
	}

	public void put(String key, Object value) {
		Assert.requiredArgument(StringUtils.isNotEmpty(key), "key");
		String path = ZooKeeperUtils.cleanPath(parentPath, key);
		ZooKeeperUtils.createNotExist(zooKeeper, path, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		byte[] data = getSerializer().serialize(value);
		ZooKeeperUtils.setData(zooKeeper, path, data);
	}

	public void put(String key, ValueWrapper value) {
		put(key, value.getAsString());
	}

	public boolean putIfAbsent(String key, ValueWrapper value) {
		return putIfAbsent(key, value.getAsString());
	}

	public boolean putIfAbsent(String key, Object value) {
		if (StringUtils.isEmpty(key)) {
			return false;
		}

		String path = ZooKeeperUtils.cleanPath(parentPath, key);
		boolean success = ZooKeeperUtils.createNotExist(zooKeeper, path, ZooDefs.Ids.OPEN_ACL_UNSAFE,
				CreateMode.PERSISTENT);
		if (!success) {
			return false;
		}
		byte[] data = getSerializer().serialize(value);
		return ZooKeeperUtils.setData(zooKeeper, path, data);
	}

	@Override
	public BroadcastEventRegistry<ChangeEvent<Elements<String>>> getKeyEventRegistry() {
		// TODO Auto-generated method stub
		return null;
	}

}
