package scw.nacos.client;

import java.util.Collections;
import java.util.Iterator;

import scw.event.ChangeEvent;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.value.ConfigurablePropertyFactory;
import scw.value.ListenablePropertyFactory;
import scw.value.StringValue;
import scw.value.Value;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;

/**
 * 使用nacos实现配置中心
 * {@link https://nacos.io/zh-cn/docs/sdk.html}
 * @author shuchaowen
 *
 */
public class NacosPropertyFactory implements ListenablePropertyFactory, ConfigurablePropertyFactory{
	private final ConfigService configService;
	private final String group;
	private final long timeoutMs;
	
	public NacosPropertyFactory(ConfigService configService, String group, long timeoutMs){
		this.configService = configService;
		this.group = group;
		this.timeoutMs = timeoutMs;
	}

	public Iterator<String> iterator() {
		return Collections.emptyIterator();
	}

	public boolean containsKey(String key) {
		return getValue(key) != null;
	}

	public Value getValue(String key) {
		String value;
		try {
			value = configService.getConfig(key, group, timeoutMs);
		} catch (NacosException e) {
			throw new RuntimeException(key, e);
		}
		return value == null? null:new StringValue(value);
	}

	public EventRegistration registerListener(String name,
			EventListener<ChangeEvent<String>> eventListener) {
		NacosConfigListener listener = new NacosConfigListener(eventListener);
		try {
			configService.addListener(name, group, listener);
		} catch (NacosException e) {
			throw new NacosConfigException(name, e);
		}
		
		return new NacosConfigEventRegistration(configService, name, group, listener);
	}

	public boolean put(String key, Value value) {
		try {
			return configService.publishConfig(key, group, value.getAsString());
		} catch (NacosException e) {
			throw new RuntimeException(key + "=" + value, e);
		}
	}

	public boolean putIfAbsent(String key, Value value) {
		if(containsKey(key)){
			return false;
		}
		
		try {
			return configService.publishConfig(key, group, value.getAsString());
		} catch (NacosException e) {
			throw new RuntimeException(key + "=" + value, e);
		}
	}

	public boolean remove(String key) {
		try {
			return configService.removeConfig(key, group);
		} catch (NacosException e) {
			throw new RuntimeException(key, e);
		}
	}

	public boolean put(String key, Object value) {
		try {
			return configService.publishConfig(key, group, value.toString());
		} catch (NacosException e) {
			throw new RuntimeException(key + "=" + value, e);
		}
	}

	public boolean putIfAbsent(String key, Object value) {
		if(containsKey(key)){
			return false;
		}
		
		try {
			return configService.publishConfig(key, group, value.toString());
		} catch (NacosException e) {
			throw new RuntimeException(key + "=" + value, e);
		}
	}

}
