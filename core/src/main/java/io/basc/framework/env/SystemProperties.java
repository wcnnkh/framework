package io.basc.framework.env;

import io.basc.framework.mapper.Property;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.element.ConvertibleEnumeration;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.element.MultiIterator;
import io.basc.framework.value.Value;
import lombok.Data;

public class SystemProperties extends DefaultPropertyResolver {
	public static volatile SystemProperties instance;

	public static SystemProperties getInstance() {
		if (instance == null) {
			synchronized (SystemProperties.class) {
				if (instance == null) {
					instance = new SystemProperties();
				}
			}
		}
		return instance;
	}

	private SystemProperties() {
		super();
	}

	@Override
	public Value get(String key) {
		Value value = super.get(key);
		if (value != null && value.isPresent()) {
			return value;
		}

		String systemProperty = System.getProperty(key);
		if (systemProperty == null) {
			systemProperty = System.getenv(key);
		}
		return Value.of(systemProperty);
	}

	@Override
	public Elements<String> keys() {
		Elements<String> systemKeys = Elements.of(() -> new MultiIterator<String>(
				CollectionUtils
						.toIterator(ConvertibleEnumeration.convertToStringEnumeration(System.getProperties().keys())),
				System.getenv().keySet().iterator()));
		return super.keys().concat(systemKeys);
	}

	@Data
	private static class SystemProperty implements Property {
		private final String name;

		@Override
		public Object getSource() {
			return getInstance().get(name);
		}

		@Override
		public Property rename(String name) {
			return new SystemProperty(name);
		}
	}

	/**
	 * 返回一个系统属性
	 * 
	 * @param key
	 * @return 不会为空
	 */
	public static Property getProperty(String key) {
		return new SystemProperty(key);
	}
}
