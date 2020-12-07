package scw.zookeeper;

import java.util.Iterator;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import scw.compatible.CompatibleUtils;
import scw.config.CloudPropertyFactory;
import scw.core.Constants;
import scw.core.instance.annotation.Configuration;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.NamedEventDispatcher;
import scw.event.support.EventType;
import scw.event.support.StringNamedEventDispatcher;
import scw.event.support.ValueEvent;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.value.StringValue;
import scw.value.Value;
import scw.value.property.PropertyEvent;

/**
 * 使用zookeeper实现的配置中心
 * @author asus1
 *
 */
@Configuration(order=Integer.MIN_VALUE)
public class ZookeeperCloudPropertyFactory implements CloudPropertyFactory, Watcher{
	private static Logger logger = LoggerFactory.getLogger(ZookeeperCloudPropertyFactory.class);
	private final NamedEventDispatcher<String, PropertyEvent> eventDispatcher = new StringNamedEventDispatcher<PropertyEvent>(true);
	private final ZooKeeper zooKeeper;
	private final String parentPath;
	
	public ZookeeperCloudPropertyFactory(ZooKeeper zooKeeper, String parentPath){
		this.zooKeeper = zooKeeper;
		this.parentPath = ZooKeeperUtils.cleanPath(parentPath);
		zooKeeper.register(this);
	}
	
	public Iterator<String> iterator() {
		return ZooKeeperUtils.getChildren(zooKeeper, parentPath).iterator();
	}

	public boolean containsKey(String key) {
		String path = ZooKeeperUtils.cleanPath(parentPath, key);
		return ZooKeeperUtils.isExist(zooKeeper, path);
	}

	public EventRegistration registerListener(String key,
			EventListener<PropertyEvent> eventListener) {
		return eventDispatcher.registerListener(key, eventListener);
	}

	public Value getValue(String key) {
		String path = ZooKeeperUtils.cleanPath(parentPath, key);
		byte[] data = ZooKeeperUtils.getData(zooKeeper, path);
		String text = CompatibleUtils.getStringOperations().createString(data, Constants.UTF_8);
		return new StringValue(text);
	}

	public boolean remove(String key) {
		String path = ZooKeeperUtils.cleanPath(parentPath, key);
		return ZooKeeperUtils.delete(zooKeeper, path, -1);
	}

	public void process(WatchedEvent event) {
		if (logger.isTraceEnabled()) {
			logger.trace(event);
		}

		String eventPath = event.getPath();
		if (eventPath == null || !eventPath.startsWith(parentPath)) {
			return;
		}
		
		String key = eventPath.substring(parentPath.length());
		if(key.startsWith("/")){
			key = key.substring(1);
		}
		
		ValueEvent<Value> valueEvent;
		switch (event.getType()) {
		case NodeDeleted:
			valueEvent = new ValueEvent<Value>(EventType.DELETE, getValue(key));
			break;
		case NodeCreated:
			valueEvent = new ValueEvent<Value>(EventType.CREATE, getValue(key));
			break;
		default:
			valueEvent = new ValueEvent<Value>(EventType.UPDATE, getValue(key));
			break;
		}
		
		eventDispatcher.publishEvent(key, new PropertyEvent(this, key, valueEvent));
	}

	public void put(String key, String value) {
		String path = ZooKeeperUtils.cleanPath(parentPath, key);
		ZooKeeperUtils.createNotExist(zooKeeper, path, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		ZooKeeperUtils.setData(zooKeeper, path, CompatibleUtils.getStringOperations().getBytes(path, Constants.UTF_8));
	}

}
