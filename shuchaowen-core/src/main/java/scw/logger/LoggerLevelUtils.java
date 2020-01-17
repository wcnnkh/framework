package scw.logger;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Properties;

import scw.core.Constants;
import scw.core.PropertyFactory;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.resource.ResourceUtils;
import scw.util.FormatUtils;
import scw.util.KeyValuePair;
import scw.util.SimpleKeyValuePair;

public class LoggerLevelUtils {
	private static final LinkedList<KeyValuePair<String, Level>> LOGGER_LEVEL_LIST = new LinkedList<KeyValuePair<String, Level>>();
	public static final PropertyFactory PROPERTY_FACTORY = new PropertyFactory() {

		public String getProperty(String key) {
			String value = null;
			if (key.equalsIgnoreCase("default.logger.level")) {
				value = LoggerLevelUtils.getDefaultLevel().name();
			} else if (key.equalsIgnoreCase("logger.rootPath")) {
				value = SystemPropertyUtils.getWorkPath();
			}
			return value == null ? Constants.PROPERTY_FACTORY.getProperty(key) : value;
		}
	};

	private static final Level DEFAULT_LEVEL;

	static {
		String levelName = SystemPropertyUtils.getProperty("scw.logger.level");
		DEFAULT_LEVEL = StringUtils.isEmpty(levelName) ? Level.INFO : Level.valueOf(levelName);

		String loggerEnablePropertiePath = SystemPropertyUtils.getProperty("scw.logger.level.config");
		if (loggerEnablePropertiePath == null) {
			loggerEnablePropertiePath = "/logger-level.properties";
		}

		if (ResourceUtils.getResourceOperations().isExist(loggerEnablePropertiePath)) {
			FormatUtils.info(LoggerLevelUtils.class, "loading " + loggerEnablePropertiePath);
			Properties properties = ResourceUtils.getResourceOperations().getProperties(loggerEnablePropertiePath);
			for (Entry<Object, Object> entry : properties.entrySet()) {
				Object key = entry.getKey();
				if (key == null) {
					continue;
				}

				Object value = entry.getValue();
				if (value == null) {
					continue;
				}

				Level level = Level.valueOf(value.toString().toUpperCase());
				if (level == null) {
					continue;
				}

				LOGGER_LEVEL_LIST.add(new SimpleKeyValuePair<String, Level>(key.toString(), level));
			}
		}
	}

	public static Level getDefaultLevel() {
		return DEFAULT_LEVEL;
	}

	public static Level getLevel(String name) {
		ListIterator<KeyValuePair<String, Level>> iterator = LOGGER_LEVEL_LIST.listIterator(LOGGER_LEVEL_LIST.size());
		while (iterator.hasPrevious()) {
			KeyValuePair<String, Level> keyValuePair = iterator.previous();
			if (keyValuePair == null) {
				continue;
			}

			if (name.startsWith(keyValuePair.getKey())) {
				return keyValuePair.getValue();
			}
		}

		return getDefaultLevel();
	}

	public static Collection<KeyValuePair<String, Level>> getLevelConfigList() {
		return Collections.unmodifiableCollection(LOGGER_LEVEL_LIST);
	}
}
