package io.basc.framework.zookeeper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventTypes;
import io.basc.framework.event.support.SimpleNamedEventDispatcher;
import io.basc.framework.io.JavaSerializer;
import io.basc.framework.io.Serializer;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.value.ConfigurablePropertyFactory;
import io.basc.framework.value.Value;

/**
 * 使用zookeeper实现的配置中心
 * 
 * @author shuchaowen
 *
 */
@Provider(order = Integer.MIN_VALUE)
public class ZookeeperCloudPropertyFactory extends SimpleNamedEventDispatcher<String, ChangeEvent<String>>
		implements ConfigurablePropertyFactory, Watcher {
	private static Logger logger = LoggerFactory.getLogger(ZookeeperCloudPropertyFactory.class);
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

	public Iterator<String> iterator() {
		List<String> paths = ZooKeeperUtils.getChildren(zooKeeper, parentPath);
		if (CollectionUtils.isEmpty(paths)) {
			return Collections.emptyIterator();
		}

		List<String> list = new ArrayList<String>();
		for (String path : paths) {
			list.add(getKey(path));
		}
		return list.iterator();
	}

	public boolean containsKey(String key) {
		if (StringUtils.isEmpty(key)) {
			return false;
		}

		String path = ZooKeeperUtils.cleanPath(parentPath, key);
		return ZooKeeperUtils.isExist(zooKeeper, path);
	}

	public Value get(String key) {
		if (StringUtils.isEmpty(key)) {
			return Value.EMPTY;
		}

		String path = ZooKeeperUtils.cleanPath(parentPath, key);
		byte[] data = ZooKeeperUtils.getData(zooKeeper, path);
		if (data == null) {
			return Value.EMPTY;
		}

		try {
			Object value = getSerializer().deserialize(data);
			return Value.of(value);
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
		ChangeEvent<String> changeEvent;
		switch (event.getType()) {
		case NodeDeleted:
			changeEvent = new ChangeEvent<String>(EventTypes.DELETE, key);
			break;
		case NodeCreated:
			changeEvent = new ChangeEvent<String>(EventTypes.CREATE, key);
			break;
		default:
			changeEvent = new ChangeEvent<String>(EventTypes.UPDATE, key);
			break;
		}

		try {
			publishEvent(key, changeEvent);
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

	public void put(String key, Value value) {
		put(key, value.getAsString());
	}

	public boolean putIfAbsent(String key, Value value) {
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

}
