package scw.logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import scw.core.GlobalPropertyFactory;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.io.ResourceUtils;
import scw.util.FormatUtils;
import scw.util.KeyValuePair;
import scw.util.comparator.CompareUtils;

public class LoggerLevelUtils {
	private static final List<LevelConfig> LOGGER_LEVEL_LIST;
	public static final Level DEFAULT_LEVEL;

	static {
		String defaultLevel = GlobalPropertyFactory.getInstance().getString(Level.class.getName());
		DEFAULT_LEVEL = StringUtils.isEmpty(defaultLevel) ? Level.INFO : Level.valueOf(defaultLevel.toUpperCase());

		List<LevelConfig> levelList = new ArrayList<LevelConfig>();
		reader(levelList, ResourceUtils.getResourceOperations().getProperties("/scw/logger/logger-level.properties")
				.getResource());

		String loggerEnablePropertiePath = GlobalPropertyFactory.getInstance().getValue("scw.logger.level.config",
				String.class, "/logger-level.properties");

		if (ResourceUtils.getResourceOperations().isExist(loggerEnablePropertiePath)) {
			FormatUtils.info(LoggerLevelUtils.class, "loading " + loggerEnablePropertiePath);
			Properties properties = ResourceUtils.getResourceOperations().getProperties(loggerEnablePropertiePath)
					.getResource();
			reader(levelList, properties);
		}

		Comparator<LevelConfig> comparator = new Comparator<LevelConfig>() {

			public int compare(LevelConfig o1, LevelConfig o2) {
				return CompareUtils.compare(o1.getKey().length(), o2.getKey().length(), false);
			}
		};
		Collections.sort(levelList, comparator);
		LOGGER_LEVEL_LIST = Arrays.asList(levelList.toArray(new LevelConfig[0]));
	}

	private static void reader(List<LevelConfig> list, Properties properties) {
		if (properties == null) {
			return;
		}

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

			list.add(new LevelConfig(key.toString(), level));
		}
	}

	public static Level getLevel(String name) {
		if (!CollectionUtils.isEmpty(LOGGER_LEVEL_LIST)) {
			for (KeyValuePair<String, Level> keyValuePair : LOGGER_LEVEL_LIST) {
				if (keyValuePair == null) {
					continue;
				}

				if (name.startsWith(keyValuePair.getKey())) {
					return keyValuePair.getValue();
				}
			}
		}
		return DEFAULT_LEVEL;
	}

	public static List<LevelConfig> getLevelConfigList() {
		return LOGGER_LEVEL_LIST;
	}
}
