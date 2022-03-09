package io.basc.framework.env;

import java.util.Iterator;
import java.util.Properties;

import io.basc.framework.convert.ConvertibleEnumeration;
import io.basc.framework.event.Observable;
import io.basc.framework.factory.InstanceDefinition;
import io.basc.framework.factory.InstanceFactory;
import io.basc.framework.factory.ServiceLoader;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.factory.support.DefaultInstanceFactory;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.MultiIterator;
import io.basc.framework.util.XUtils;
import io.basc.framework.value.StringValue;
import io.basc.framework.value.Value;

/**
 * 不使用System这个名称的原因是名称冲突
 * 
 * @author shuchaowen
 *
 */
public final class Sys extends DefaultEnvironment implements ServiceLoaderFactory, InstanceFactory {
	/**
	 * 为了兼容老版本
	 */
	public static final String WEB_ROOT_PROPERTY = "web.root";

	private static Logger logger = LoggerFactory.getLogger(Sys.class);

	public static final Sys env = new Sys();
	static {
		env.load();
	}

	private Sys() {
	}

	private final DefaultInstanceFactory instanceFactory = new DefaultInstanceFactory(this);

	private void load() {
		/**
		 * 加载配置文件
		 */
		loadProperties("system.properties");
		loadProperties(getValue("io.basc.framework.properties", String.class, "/private.properties"));

		// 初始化日志等级管理器
		Observable<Properties> observable = getProperties(
				getValue("io.basc.framework.logger.level.properties", String.class, "/logger-level.properties"));
		LoggerFactory.getLevelManager().combine(observable);

		/**
		 * 加载默认服务
		 */
		configure(instanceFactory);
	}

	public Iterator<String> iterator() {
		return new MultiIterator<String>(super.iterator(),
				CollectionUtils
						.toIterator(ConvertibleEnumeration.convertToStringEnumeration(System.getProperties().keys())),
				System.getenv().keySet().iterator());
	}

	public Value getValue(String key) {
		Value value = super.getValue(key);
		if (value != null) {
			return value;
		}

		String v = getSystemProperty(key);
		return v == null ? null : new StringValue(v);
	}

	public String getSystemProperty(String key) {
		String value = System.getProperty(key);
		if (value == null) {
			value = System.getenv(key);
		}

		if (value == null && WEB_ROOT_PROPERTY.equals(key)) {
			value = getWorkPath();
		}
		return value;
	}

	@Override
	public boolean containsKey(String key) {
		if (super.containsKey(key)) {
			return true;
		}

		return getSystemProperty(key) != null;
	}

	/**
	 * 获取工作目录
	 */
	@Override
	public String getWorkPath() {
		String path = super.getWorkPath();
		if (path == null) {
			path = XUtils.getWebAppDirectory(getClassLoader());
			if (path != null) {
				setWorkPath(path);
				logger.info("default " + WORK_PATH_PROPERTY + " in " + path);
			}
		}
		return path;
	}

	@Override
	public <T> T getInstance(Class<T> clazz) {
		return instanceFactory.getInstance(clazz);
	}

	@Override
	public boolean isInstance(Class<?> clazz) {
		return instanceFactory.isInstance(clazz);
	}

	@Override
	public boolean isInstance(String name) {
		return instanceFactory.isInstance(name);
	}

	@Override
	public <T> T getInstance(String name) {
		return instanceFactory.getInstance(name);
	}

	@Override
	public <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass) {
		return instanceFactory.getServiceLoader(serviceClass);
	}

	@Override
	public InstanceDefinition getDefinition(String name) {
		return instanceFactory.getDefinition(name);
	}

	@Override
	public InstanceDefinition getDefinition(Class<?> clazz) {
		return instanceFactory.getDefinition(clazz);
	}

	@Override
	public boolean isInstance(String name, Object... params) {
		return instanceFactory.isInstance(name, params);
	}

	@Override
	public <T> T getInstance(String name, Object... params) {
		return instanceFactory.getInstance(name, params);
	}

	@Override
	public boolean isInstance(Class<?> clazz, Object... params) {
		return instanceFactory.isInstance(clazz, params);
	}

	@Override
	public <T> T getInstance(Class<T> clazz, Object... params) {
		return instanceFactory.getInstance(clazz, params);
	}

	@Override
	public boolean isInstance(String name, Class<?>[] parameterTypes) {
		return instanceFactory.isInstance(name, parameterTypes);
	}

	@Override
	public <T> T getInstance(String name, Class<?>[] parameterTypes, Object[] params) {
		return instanceFactory.getInstance(name, parameterTypes, params);
	}

	@Override
	public boolean isInstance(Class<?> clazz, Class<?>[] parameterTypes) {
		return instanceFactory.isInstance(clazz, parameterTypes);
	}

	@Override
	public <T> T getInstance(Class<T> clazz, Class<?>[] parameterTypes, Object[] params) {
		return instanceFactory.getInstance(clazz, parameterTypes, params);
	}
}
