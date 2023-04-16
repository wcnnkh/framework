package io.basc.framework.value;

import io.basc.framework.event.BroadcastEventDispatcher;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.support.DynamicMap;
import io.basc.framework.event.support.StandardBroadcastEventDispatcher;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Registration;
import io.basc.framework.util.concurrent.TaskQueue;

/**
 * 配置中心实现
 * 
 * @author wcnnkh
 *
 */
public class ConfigurationCenter extends PropertyFactories {
	private final ObservablePropertyFactory archive = new ObservablePropertyFactory();
	private final MapPropertyFactory master = new MapPropertyFactory();

	public ConfigurationCenter() {
		// 默认使用异步通知
		this(new StandardBroadcastEventDispatcher<>(new TaskQueue()));
	}

	public ConfigurationCenter(BroadcastEventDispatcher<ChangeEvent<Elements<String>>> keyEventDispatcher) {
		super(keyEventDispatcher);
		register(master);
		register(archive);
	}

	/**
	 * 归档数据但修改不频繁,如本地配置文件
	 * 
	 * @return
	 */
	public ObservablePropertyFactory getArchive() {
		return archive;
	}

	/**
	 * 主要的数据
	 * 
	 * @return
	 */
	public DynamicMap<String, Value> getMaster() {
		return master;
	}

	/**
	 * 重置
	 * 
	 * @return
	 */
	public Registration reset() {
		Registration registration = clear();
		registration = registration.and(register(master));
		registration = registration.and(register(archive));
		return registration;
	}

}
