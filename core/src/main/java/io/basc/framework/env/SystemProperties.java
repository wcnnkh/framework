package io.basc.framework.env;

import io.basc.framework.beans.factory.spi.SPI;
import io.basc.framework.convert.lang.ObjectValue;
import io.basc.framework.transform.Property;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.ConvertibleEnumeration;
import io.basc.framework.util.Elements;
import io.basc.framework.util.MultiIterator;
import io.basc.framework.util.XUtils;
import lombok.Data;

public class SystemProperties extends DefaultPropertyResolver {
	private static final String WORK_PATH_NAME = "basc.work.path";

	private static volatile SystemProperties instance;

	public static SystemProperties getInstance() {
		if (instance == null) {
			synchronized (SystemProperties.class) {
				if (instance == null) {
					instance = new SystemProperties();
					instance.configure(SPI.global());
				}
			}
		}
		return instance;
	}

	private SystemProperties() {
		super();
	}

	@Override
	public ObjectValue get(String key) {
		ObjectValue value = super.get(key);
		if (value != null && value.isPresent()) {
			return value;
		}

		String systemProperty = System.getProperty(key);
		if (systemProperty == null) {
			systemProperty = System.getenv(key);
		}
		return ObjectValue.of(systemProperty);
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
		public Object getValue() {
			return getInstance().get(name);
		}

		@Override
		public Property rename(String name) {
			return new SystemProperty(name);
		}
		
		@Override
		public void setValue(Object value) {
			// TODO Auto-generated method stub
			
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

	public static String getWorkPath() {
		return getInstance().get(WORK_PATH_NAME)
				.orGet(() -> XUtils.getWebAppDirectory(ClassUtils.getDefaultClassLoader())).getAsString();
	}

	public static void setWorkPath(String workPath) {
		getInstance().put(WORK_PATH_NAME, workPath);
	}
}
