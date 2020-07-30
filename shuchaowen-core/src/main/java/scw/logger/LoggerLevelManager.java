package scw.logger;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import scw.core.GlobalPropertyFactory;
import scw.core.utils.StringUtils;
import scw.event.BasicEventDispatcher;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.ObjectEvent;
import scw.event.support.BasicEvent;
import scw.event.support.DefaultBasicEventDispatcher;
import scw.io.ClassPathResource;
import scw.io.ResourceUtils;
import scw.io.support.DynamicProperties;

public class LoggerLevelManager {
	public static final Map<String, Level> DEFAULT_LEVEL_MAP;

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
		DEFAULT_LEVEL_MAP = Collections.unmodifiableMap(parse(properties,
				defLevel));

		loggerLevelManager = new LoggerLevelManager(defLevel);
		loggerLevelManager.loadProperties(GlobalPropertyFactory.getInstance()
				.getValue("scw.logger.level.config", String.class,
						"/logger-level.properties"));
	}

	public static LoggerLevelManager getInstance() {
		return loggerLevelManager;
	}

	private final Level defaultLevel;
	private volatile TreeMap<String, Level> levelMap;
	private final BasicEventDispatcher<BasicEvent> eventDispatcher = new DefaultBasicEventDispatcher<BasicEvent>(
			true);
	private final DynamicProperties dynamicProperties = new DynamicProperties();

	private LoggerLevelManager(Level defaultLevel) {
		this.defaultLevel = defaultLevel;
		dynamicProperties.getEventDispatcher().registerListener(
				new EventListener<ObjectEvent<Properties>>() {
					public void onEvent(ObjectEvent<Properties> event) {
						levelMap = parse(event.getSource(), getDefaultLevel());
						eventDispatcher.publishEvent(new BasicEvent());
					};
				});
	}

	public boolean loadProperties(String properties) {
		return dynamicProperties.load(properties);
	}

	private static TreeMap<String, Level> parse(Properties properties,
			Level defaultLevel) {
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
		return levelMap;
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
		Level level = levelMap == null ? null : getLevel(levelMap, name);
		if (level == null) {
			level = getLevel(DEFAULT_LEVEL_MAP, name);
		}
		return level == null ? defaultLevel : level;
	}

	public DynamicLevel getDynamicLevel(String name) {
		return new DynamicLevel(name);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Level> getLevelMap() {
		return (Map<String, Level>) (levelMap == null ? Collections.emptyMap()
				: Collections.unmodifiableMap(levelMap));
	}

	public BasicEventDispatcher<BasicEvent> getEventDispatcher() {
		return eventDispatcher;
	}

	public final class DynamicLevel implements EventRegistration {
		private volatile Level level;
		private EventRegistration eventRegistration;
		private BasicEventDispatcher<BasicEvent> eventDispatcher;

		public DynamicLevel(final String name) {
			this.level = LoggerLevelManager.this.getLevel(name);
			this.eventRegistration = LoggerLevelManager.this.eventDispatcher
					.registerListener(new EventListener<BasicEvent>() {

						public void onEvent(BasicEvent event) {
							Level level = LoggerLevelManager.this
									.getLevel(name);
							if (!level.equals(DynamicLevel.this.level)) {
								DynamicLevel.this.level = level;
								if (eventDispatcher != null) {
									eventDispatcher
											.publishEvent(new BasicEvent());
								}
							}
						}
					});
		}

		public void unregister() {
			eventRegistration.unregister();
		}

		public Level getLevel() {
			return level;
		}

		public BasicEventDispatcher<BasicEvent> getEventDispatcher() {
			if (eventDispatcher == null) {
				eventDispatcher = new DefaultBasicEventDispatcher<BasicEvent>(
						false);
			}
			return eventDispatcher;
		}
	}
}
