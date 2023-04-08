package io.basc.framework.nacos.client;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;

import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventListener;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Registration;
import io.basc.framework.value.ConfigurablePropertyFactory;
import io.basc.framework.value.Value;

/**
 * 使用nacos实现配置中心
 * 
 * @deprecated
 * @author wcnnkh
 *
 */
public class NacosPropertyFactory implements ConfigurablePropertyFactory {
	private final ConfigService configService;
	private final String group;
	private final long timeoutMs;

	public NacosPropertyFactory(ConfigService configService, String group, long timeoutMs) {
		this.configService = configService;
		this.group = group;
		this.timeoutMs = timeoutMs;
	}

	@Override
	public Elements<String> keys() {
		return Elements.empty();
	}

	public boolean containsKey(String key) {
		return get(key).isPresent();
	}

	public Value get(String key) {
		String value;
		try {
			value = configService.getConfig(key, group, timeoutMs);
		} catch (NacosException e) {
			throw new RuntimeException(key, e);
		}
		return Value.of(value);
	}

	@Override
	public Registration registerListener(String name, EventListener<ChangeEvent<String>> eventListener) {
		NacosConfigListener listener = new NacosConfigListener(eventListener);
		try {
			configService.addListener(name, group, listener);
		} catch (NacosException e) {
			throw new NacosConfigException(name, e);
		}

		return new NacosConfigEventRegistration(configService, name, group, listener);
	}

	public void put(String key, Value value) {
		try {
			configService.publishConfig(key, group, value.getAsString());
		} catch (NacosException e) {
			throw new RuntimeException(key + "=" + value, e);
		}
	}

	public boolean putIfAbsent(String key, Value value) {
		if (containsKey(key)) {
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

	public void put(String key, Object value) {
		try {
			configService.publishConfig(key, group, value.toString());
		} catch (NacosException e) {
			throw new RuntimeException(key + "=" + value, e);
		}
	}

	public boolean putIfAbsent(String key, Object value) {
		if (containsKey(key)) {
			return false;
		}

		try {
			return configService.publishConfig(key, group, value.toString());
		} catch (NacosException e) {
			throw new RuntimeException(key + "=" + value, e);
		}
	}

}
