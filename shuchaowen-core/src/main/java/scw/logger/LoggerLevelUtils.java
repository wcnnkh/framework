package scw.logger;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Properties;

import scw.core.Constants;
import scw.core.GlobalPropertyFactory;
import scw.core.utils.StringUtils;
import scw.io.ResourceUtils;
import scw.util.FormatUtils;
import scw.util.KeyValuePair;
import scw.util.comparator.CompareUtils;
import scw.util.value.StringValue;
import scw.util.value.property.NotSupportEnumerationPropertyFactory;
import scw.util.value.property.PropertyFactory;

public class LoggerLevelUtils {
	private static final LinkedList<KeyValuePair<String, Level>> LOGGER_LEVEL_LIST = new LinkedList<KeyValuePair<String, Level>>();
	public static final PropertyFactory PROPERTY_FACTORY = new NotSupportEnumerationPropertyFactory() {

		public scw.util.value.Value get(String key) {
			String value = null;
			if (key.equalsIgnoreCase("default.logger.level")) {
				value = DEFAULT_LEVEL.name();
			} else if (key.equalsIgnoreCase("logger.rootPath")) {
				value = GlobalPropertyFactory.getInstance().getWorkPath();
			}
			return value == null ? Constants.PROPERTY_FACTORY.get(key)
					: new StringValue(value);
		};
	};

	public static final Level DEFAULT_LEVEL;

	static {
		String defaultLevel = GlobalPropertyFactory.getInstance().getString(
				Level.class.getName());
		DEFAULT_LEVEL = StringUtils.isEmpty(defaultLevel) ? Level.INFO : Level
				.valueOf(defaultLevel.toUpperCase());

		reader(ResourceUtils.getResourceOperations().getFormattedProperties(
				"/scw/logger/logger-level.properties", PROPERTY_FACTORY));

		String loggerEnablePropertiePath = GlobalPropertyFactory.getInstance()
				.getValue("scw.logger.level.config", String.class,
						"/logger-level.properties");
		if (ResourceUtils.getResourceOperations().isExist(
				loggerEnablePropertiePath)) {
			FormatUtils.info(LoggerLevelUtils.class, "loading "
					+ loggerEnablePropertiePath);
			Properties properties = ResourceUtils.getResourceOperations()
					.getFormattedProperties(loggerEnablePropertiePath,
							PROPERTY_FACTORY);
			reader(properties);
		}

		Comparator<KeyValuePair<String, Level>> comparator = new Comparator<KeyValuePair<String, Level>>() {

			public int compare(KeyValuePair<String, Level> o1,
					KeyValuePair<String, Level> o2) {
				return CompareUtils.compare(o1.getKey().length(), o2.getKey()
						.length(), false);
			}
		};
		Collections.sort(LOGGER_LEVEL_LIST, comparator);
	}

	private static void reader(Properties properties) {
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

			LOGGER_LEVEL_LIST.add(new KeyValuePair<String, Level>(key
					.toString(), level));
		}
	}

	public static Level getLevel(String name) {
		for (KeyValuePair<String, Level> keyValuePair : LOGGER_LEVEL_LIST) {
			if (keyValuePair == null) {
				continue;
			}

			if (name.startsWith(keyValuePair.getKey())) {
				return keyValuePair.getValue();
			}
		}
		return DEFAULT_LEVEL;
	}

	public static Collection<KeyValuePair<String, Level>> getLevelConfigList() {
		return Collections.unmodifiableCollection(LOGGER_LEVEL_LIST);
	}
}
