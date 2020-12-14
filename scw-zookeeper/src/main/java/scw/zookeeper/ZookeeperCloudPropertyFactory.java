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

import scw.config.CloudPropertyFactory;
import scw.core.Constants;
import scw.core.instance.annotation.SPI;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.event.EventType;
import scw.event.KeyValuePairEvent;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.value.EmptyValue;
import scw.value.StringValue;
import scw.value.Value;
import scw.value.property.AbstractBasePropertyFactory;
import scw.value.property.PropertyEvent;

/**
 * 使用zookeeper实现的配置中心
 * @author asus1
 *
 */
@SPI(order=Integer.MIN_VALUE)
public class ZookeeperCloudPropertyFactory extends AbstractBasePropertyFactory implements CloudPropertyFactory, Watcher{
	private static Logger logger = LoggerFactory.getLogger(ZookeeperCloudPropertyFactory.class);
	private final ZooKeeper zooKeeper;
	private final String parentPath;
	
	public ZookeeperCloudPropertyFactory(ZooKeeper zooKeeper, String parentPath){
		super(true);
		this.zooKeeper = zooKeeper;
		this.parentPath = ZooKeeperUtils.cleanPath(parentPath);
		zooKeeper.register(this);
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
		String text = StringUtils.getStringOperations().createString(data, Constants.UTF_8);
		return new StringValue(text);
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
			logger.trace(event);
		}

		String eventPath = event.getPath();
		if (eventPath == null || !eventPath.startsWith(parentPath)) {
			return;
		}
		
		String key = getKey(eventPath);
		KeyValuePairEvent<String, Value> keyValuePairEvent;
		switch (event.getType()) {
		case NodeDeleted:
			keyValuePairEvent = new KeyValuePairEvent<String, Value>(EventType.DELETE, key, getValue(key));
			break;
		case NodeCreated:
			keyValuePairEvent = new KeyValuePairEvent<String, Value>(EventType.CREATE, key, getValue(key));
			break;
		default:
			keyValuePairEvent = new KeyValuePairEvent<String, Value>(EventType.UPDATE, key, getValue(key));
			break;
		}
		
		getEventDispatcher().publishEvent(key, new PropertyEvent(this, keyValuePairEvent));
	}

	public boolean put(String key, String value) {
		if(StringUtils.isEmpty(key)){
			return false;
		}
		
		String path = ZooKeeperUtils.cleanPath(parentPath, key);
		ZooKeeperUtils.createNotExist(zooKeeper, path, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		return ZooKeeperUtils.setData(zooKeeper, path, StringUtils.getStringOperations().getBytes(value, Constants.UTF_8));
	}

}
