package run.soeasy.framework.util.logging;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.util.KeyValue;
import run.soeasy.framework.util.StringUtils;
import run.soeasy.framework.util.collections.Elements;
import run.soeasy.framework.util.collections.Reloadable;
import run.soeasy.framework.util.match.StringMatcher;
import run.soeasy.framework.util.match.StringMatchers;

public class LevelRegistry implements LevelFactory, Reloadable {
	@NonNull
	private volatile StringMatcher nameMatcher = StringMatchers.PREFIX;
	private volatile Map<String, Level> levelMap;
	@Getter
	@Setter
	@NonNull
	private Level defaultLevel = Levels.INFO.getValue();

	public StringMatcher getNameMatcher() {
		return nameMatcher;
	}

	public void setNameMatcher(@NonNull StringMatcher nameMatcher) {
		synchronized (this) {
			this.nameMatcher = nameMatcher;
			reload();
		}
	}

	public Elements<KeyValue<String, Level>> getLevels() {
		synchronized (this) {
			if (levelMap == null) {
				return Elements.empty();
			}
			return Elements.of(levelMap.entrySet()).map((e) -> KeyValue.of(e.getKey(), e.getValue()));
		}
	}

	@Override
	public void reload() {
		synchronized (this) {
			if (levelMap == null) {
				return;
			}

			Map<String, Level> backMap = new LinkedHashMap<>(levelMap);
			levelMap = new TreeMap<String, Level>(nameMatcher);
			levelMap.putAll(backMap);
		}
	}

	public void setLevel(@NonNull String name, Level level) {
		synchronized (this) {
			if (levelMap == null) {
				levelMap = new TreeMap<String, Level>(nameMatcher);
			} else {
				Iterator<Entry<String, Level>> iterator = levelMap.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<String, Level> entry = iterator.next();
					if (StringUtils.equals(entry.getKey(), name)) {
						continue;
					}

					if (match(name, entry.getKey())) {
						iterator.remove();
					}
				}
			}
			levelMap.put(name, level);
		}
	}

	public boolean match(String pattern, String name) {
		return StringUtils.equals(pattern, name) || nameMatcher.match(pattern, name);
	}

	private Level internalGetLevel(String name) {
		Level level = levelMap.get(name);
		if (level != null) {
			return level;
		}

		for (Entry<String, Level> entry : levelMap.entrySet()) {
			if (match(entry.getKey(), name)) {
				return entry.getValue();
			}
		}
		return null;
	}

	@Override
	public Level getLevel(@NonNull String name) {
		Level level = levelMap == null ? null : internalGetLevel(name);
		return level == null ? defaultLevel : level;
	}

}
