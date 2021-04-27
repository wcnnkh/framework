package scw.zookeeper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import scw.context.annotation.Provider;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.event.ChangeEvent;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.EventType;
import scw.event.NamedEventDispatcher;
import scw.event.support.StringNamedEventDispatcher;
import scw.io.JavaSerializer;
import scw.io.Serializer;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.value.AnyValue;
import scw.value.ConfigurablePropertyFactory;
import scw.value.EmptyValue;
import scw.value.ListenablePropertyFactory;
import scw.value.Value;

/**
 * 使用zookeeper实现的配置中心
 * @author shuchaowen
 *
 */
@Provider(order=Integer.MIN_VALUE)
public class ZookeeperCloudPropertyFactory implements ListenablePropertyFactory, ConfigurablePropertyFactory, Watcher{
	private static Logger logger = LoggerFactory.getLogger(ZookeeperCloudPropertyFactory.class);
	private final NamedEventDispatcher<String, ChangeEvent<String>> eventDispatcher = new StringNamedEventDispatcher<ChangeEvent<String>>(true);
	private final ZooKeeper zooKeeper;
	private final String parentPath;
	private Serializer serializer;
	
	public ZookeeperCloudPropertyFactory(ZooKeeper zooKeeper, String parentPath){
		this.zooKeeper = zooKeeper;
		this.parentPath = ZooKeeperUtils.cleanPath(parentPath);
		zooKeeper.register(this);
	}
	
	public Serializer getSerializer() {
		return serializer == null? JavaSerializer.INSTANCE:serializer;
	}

	public void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}

	private String getKey(String path){
		String key = path.substring(parentPath.length());
		if(key.startsWith("/")){
			key = key.substring(1);
		}
		return key;
	}
	
	public Iterator<String> iterator() {
		List<String> paths = ZooKeeperUtils.getChildren(zooKeeper, parentPath);
		if(CollectionUtils.isEmpty(paths)){
			return Collections.emptyIterator();
		}
		
		List<String> list = new ArrayList<String>();
		for(String path : paths){
			list.add(getKey(path));
		}
		return list.iterator();
	}

	public boolean containsKey(String key) {
		if(StringUtils.isEmpty(key)){
			return false;
		}
		
		String path = ZooKeeperUtils.cleanPath(parentPath, key);
		return ZooKeeperUtils.isExist(zooKeeper, path);
	}

	public Value getValue(String key) {
		if(StringUtils.isEmpty(key)){
			return EmptyValue.INSTANCE;
		}
		
		String path = ZooKeeperUtils.cleanPath(parentPath, key);
		byte[] data = ZooKeeperUtils.getData(zooKeeper, path);
		if(data == null){
			return EmptyValue.INSTANCE;
		}
		
		try {
			Object value = getSerializer().deserialize(data);
			return new AnyValue(value);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean remove(String key) {
		if(StringUtils.isEmpty(key)){
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
			changeEvent = new ChangeEvent<String>(EventType.DELETE, key);
			break;
		case NodeCreated:
			changeEvent = new ChangeEvent<String>(EventType.CREATE, key);
			break;
		default:
			changeEvent = new ChangeEvent<String>(EventType.UPDATE, key);
			break;
		}
		
		eventDispatcher.publishEvent(key, changeEvent);
	}

	public boolean put(String key, Object value) {
		if(StringUtils.isEmpty(key)){
			return false;
		}
		
		String path = ZooKeeperUtils.cleanPath(parentPath, key);
		ZooKeeperUtils.createNotExist(zooKeeper, path, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		byte[] data = getSerializer().serialize(value);
		return ZooKeeperUtils.setData(zooKeeper, path, data);
	}

	public EventRegistration registerListener(String name,
			EventListener<ChangeEvent<String>> eventListener) {
		return eventDispatcher.registerListener(name, eventListener);
	}

	public boolean put(String key, Value value) {
		return put(key, value.getAsString());
	}

	public boolean putIfAbsent(String key, Value value) {
		return putIfAbsent(key, value.getAsString());
	}

	public boolean putIfAbsent(String key, Object value) {
		if(StringUtils.isEmpty(key)){
			return false;
		}
		
		String path = ZooKeeperUtils.cleanPath(parentPath, key);
		boolean success = ZooKeeperUtils.createNotExist(zooKeeper, path, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		if(!success){
			return false;
		}
		byte[] data = getSerializer().serialize(value);
		return ZooKeeperUtils.setData(zooKeeper, path, data);
	}

}
