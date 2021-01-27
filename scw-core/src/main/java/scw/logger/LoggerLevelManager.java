package scw.logger;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.env.SystemEnvironment;
import scw.event.Observable;
import scw.io.Resource;
import scw.io.ResourceUtils;
import scw.io.event.ConvertibleObservablesProperties;

public class LoggerLevelManager extends
		ConvertibleObservablesProperties<SortedMap<String, Level>> {
	private static final SortedMap<String, Level> DEFAULT_LEVEL_MAP;
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
		String defaultLevel = SystemEnvironment.getInstance().getString(
				Level.class.getName());
		Level defLevel = StringUtils.isEmpty(defaultLevel) ? Level.INFO : Level
				.getLevel(defaultLevel.toUpperCase());
		
		TreeMap<String, Level> levelMap = new TreeMap<String, Level>(LEVEL_NAME_COMPARATOR);	
		try {
			for(Resource resource : ResourceUtils.getSystemResources("scw/logger-level.properties")){
				Properties properties = new Properties();
				SystemEnvironment.getInstance().resolveProperties(properties, resource, null);
				load(levelMap, properties, defLevel, true);
			}
		} catch (IOException e) {
		}
		if(levelMap.isEmpty()){
			DEFAULT_LEVEL_MAP = Collections.emptySortedMap();
		}else{
			DEFAULT_LEVEL_MAP = Collections.unmodifiableSortedMap(levelMap);
		}
		
		loggerLevelManager = new LoggerLevelManager(defLevel);
		Observable<Properties> observable = SystemEnvironment.getInstance().getProperties(SystemEnvironment.getInstance().getValue(
						"scw.logger.level.config", String.class,
						"/logger-level.properties"));
		observable.register();
		loggerLevelManager.addObservable(observable);
	}
	
	

	public static LoggerLevelManager getInstance() {
		return loggerLevelManager;
	}

	private final Level defaultLevel;

	private LoggerLevelManager(Level defaultLevel) {
		super(true);
		this.defaultLevel = defaultLevel;
	}
	
	@Override
	public SortedMap<String, Level> forceGet() {
		TreeMap<String, Level> map = new TreeMap<String, Level>(LEVEL_NAME_COMPARATOR);
		map.putAll(DEFAULT_LEVEL_MAP);
		map.putAll(super.forceGet());
		return map;
	}

	public SortedMap<String, Level> convert(Properties properties) {
		return parse(properties, defaultLevel, false);
	}
	
	private static SortedMap<String, Level> parse(Properties properties,
			Level defaultLevel, boolean ignore){
		if (CollectionUtils.isEmpty(properties)) {
			return Collections.emptySortedMap();
		}
		
		TreeMap<String, Level> levelMap = new TreeMap<String, Level>(LEVEL_NAME_COMPARATOR);
		load(levelMap, properties, defaultLevel, ignore);
		if (levelMap.isEmpty()) {
			return Collections.emptySortedMap();
		}
		return Collections.unmodifiableSortedMap(levelMap);
	}

	private static void load(Map<String, Level> levelMap, Properties properties,
			Level defaultLevel, boolean ignore) {
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
			
			putLevel(levelMap, String.valueOf(key), level, ignore);
		}
	}

	public Level getDefaultLevel() {
		return defaultLevel;
	}
	
	private static void putLevel(Map<String, Level> levelMap, String name, Level level, boolean ignore){
		if(ignore){
			Level cacheLevel = levelMap.get(name);
			//忽略低级的配置。 比如原来是DEBUG(cacheLevel),现在是INFO(level),那么不插入此配置
			if(cacheLevel != null && level.isGreaterOrEqual(cacheLevel)){
				return ;
			}
		}
		levelMap.put(name, level);
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
}
