package scw.logger;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;

import scw.core.utils.CollectionUtils;
import scw.event.BasicEvent;
import scw.event.support.DefaultBasicEventDispatcher;

public class LevelManager extends DefaultBasicEventDispatcher<BasicEvent> {
	public static final Comparator<String> LEVEL_NAME_COMPARATOR = new Comparator<String>() {
		public int compare(String o1, String o2) {
			if (o1.equals(o2)) {
				return 0;
			}

			return o1.length() > o2.length() ? -1 : 1;
		};
	};

	private volatile TreeMap<String, Level> levelMap = new TreeMap<String, Level>(LEVEL_NAME_COMPARATOR);

	public LevelManager() {
		super(true);
	}

	public Level getLevel(String name) {
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

	/**
	 * 是否存在此定义
	 * 
	 * @param name
	 * @return
	 */
	public boolean exists(String name) {
		for (String key : levelMap.keySet()) {
			if (name.equals(key) || name.startsWith(key)) {
				return true;
			}
		}
		return false;
	}

	public Level getMaxLevel() {
		Level maxLevel = null;
		for (Level level : levelMap.values()) {
			if (maxLevel == null) {
				maxLevel = level;
				continue;
			}

			if (CustomLevel.isGreaterOrEqual(level, maxLevel)) {
				maxLevel = level;
			}
		}
		return maxLevel;
	}

	public Level getMinLevel() {
		Level minLevel = null;
		for (Level level : levelMap.values()) {
			if (minLevel == null) {
				minLevel = level;
				continue;
			}

			if (!CustomLevel.isGreaterOrEqual(level, minLevel)) {
				minLevel = level;
			}
		}
		return minLevel;
	}

	public SortedMap<String, Level> getLevelMap() {
		return Collections.unmodifiableSortedMap(levelMap);
	}

	public void setLevelMap(Map<String, Level> levelMap) {
		TreeMap<String, Level> sortedMap = new TreeMap<String, Level>(LEVEL_NAME_COMPARATOR);
		if (!CollectionUtils.isEmpty(levelMap)) {
			sortedMap.putAll(levelMap);
		}
		synchronized (this.levelMap) {
			this.levelMap = sortedMap;
		}
		publishEvent(new BasicEvent());
	}

	public void put(String name, Level level) {
		synchronized (this.levelMap) {
			levelMap.put(name, level);
		}
		publishEvent(new BasicEvent());
	}

	public void remove(String name) {
		synchronized (this.levelMap) {
			if (levelMap.remove(name) != null) {
				publishEvent(new BasicEvent());
			}
		}
	}
}
