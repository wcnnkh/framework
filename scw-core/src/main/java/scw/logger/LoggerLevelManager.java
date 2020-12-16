package scw.logger;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import scw.core.GlobalPropertyFactory;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.io.ClassPathResource;
import scw.io.ResourceUtils;
import scw.io.event.ConvertibleObservablesProperties;

public class LoggerLevelManager extends
		ConvertibleObservablesProperties<SortedMap<String, Level>> {
	public static final SortedMap<String, Level> DEFAULT_LEVEL_MAP;

	private static final Comparator<String> LEVEL_NAME_COMPARATOR = new Comparator<String>() {
		public int compare(String o1, String o2) {
			if (o1.equals(o2)) {
				return 0;
			}

			return o1.length() > o2.length() ? -1 : 1;
		};
	};

	private static LoggerLevelManager loggerLevelManager;

	static {
		String defaultLevel = GlobalPropertyFactory.getInstance().getString(
				Level.class.getName());
		Level defLevel = StringUtils.isEmpty(defaultLevel) ? Level.INFO : Level
				.getLevel(defaultLevel.toUpperCase());

		ClassPathResource resource = new ClassPathResource(
				"scw/logger/logger-level.properties");
		Properties properties = new Properties();
		ResourceUtils.loadProperties(properties, resource, null);
		DEFAULT_LEVEL_MAP = parse(properties, defLevel);

		loggerLevelManager = new LoggerLevelManager(defLevel);
		loggerLevelManager.loadProperties(
				GlobalPropertyFactory.getInstance().getValue(
						"scw.logger.level.config", String.class,
						"/logger-level.properties")).register();
	}

	public static LoggerLevelManager getInstance() {
		return loggerLevelManager;
	}

	private final Level defaultLevel;
	private LoggerLevelManager(Level defaultLevel) {
		super(true);
		this.defaultLevel = defaultLevel;
	}

	public SortedMap<String, Level> convert(Properties properties) {
		return parse(properties, defaultLevel);
	}

	private static SortedMap<String, Level> parse(Properties properties,
			Level defaultLevel) {
		if (CollectionUtils.isEmpty(properties)) {
			return Collections.emptySortedMap();
		}

		TreeMap<String, Level> levelMap = new TreeMap<String, Level>(
				LEVEL_NAME_COMPARATOR);
		for (Entry<Object, Object> entry : properties.entrySet()) {
			Object key = entry.getKey();
			if (key == null) {
				continue;
			}

			Object value = entry.getValue();
			if (value == null) {
				continue;
			}

			Level level = Level.getLevel(value.toString(),
					defaultLevel.getValue());
			if (level == null) {
				continue;
			}

			levelMap.put(key.toString(), level);
		}

		if (levelMap.isEmpty()) {
			return Collections.emptySortedMap();
		}
		return Collections.unmodifiableSortedMap(levelMap);
	}

	public Level getDefaultLevel() {
		return defaultLevel;
	}

	private static Level getLevel(Map<String, Level> levelMap, String name) {
		Level level = levelMap.get(name);
		if (level != null) {
			return level;
		}

		for (Entry<String, Level> entry : levelMap.entrySet()) {
			if (name.startsWith(entry.getKey())) {
				return entry.getValue();
			}
		}
		return null;
	}

	public Level getLevel(String name) {
		Level level = getLevel(get(), name);
		if (level == null) {
			level = getLevel(DEFAULT_LEVEL_MAP, name);
		}
		return level == null ? defaultLevel : level;
	}

	@Override
	public SortedMap<String, Level> get() {
		SortedMap<String, Level> map = super.get();
		if (map == null) {
			return Collections.emptySortedMap();
		}
		return map;
	}

	public SortedMap<String, Level> getLevelMap() {
		return get();
	}
}
