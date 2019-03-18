package scw.db;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Properties;

import scw.common.utils.ClassUtils;
import scw.common.utils.ConfigUtils;
import scw.common.utils.PropertiesUtils;
import scw.core.NestedRuntimeException;

public final class DBUtils {
	private DBUtils() {
	};

	public static <T> void loadProeprties(Class<T> type, T instance,
			String propertiesFile, String charsetName) {
		Properties properties = ConfigUtils.getProperties(propertiesFile,
				charsetName);
		PropertiesUtils.invokeSetterByProeprties(type, instance, properties,
				false, true, Arrays.asList("jdbcUrl,url,host", "username,user",
						"password", "minSize,initialSize,minimumIdle",
						"maxSize,maxActive",
						"driver,driverClass,driverClassName"), true);
	}

	public static <T> void loadProperties(Class<T> type, T instance,
			String propertiesFile) {
		loadProeprties(type, instance, propertiesFile, "UTF-8");
	}

	public static <T> T newInstanceAndLoadProperties(Class<T> type,
			String propertiesFile) {
		T instance;
		try {
			instance = ClassUtils.newInstance(type);
		} catch (InstantiationException e) {
			throw new NestedRuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new NestedRuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new NestedRuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new NestedRuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new NestedRuntimeException(e);
		} catch (SecurityException e) {
			throw new NestedRuntimeException(e);
		}

		loadProperties(type, instance, propertiesFile);
		return instance;
	}
}
