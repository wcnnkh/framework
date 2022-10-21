package io.basc.framework.logger;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.logging.Level;

import io.basc.framework.event.support.StandardObservableProperties;
import io.basc.framework.lang.Nullable;

/**
 * 动态管理日志等级管理<br/>
 * 
 * @author shuchaowen
 *
 */
public class LevelManager extends StandardObservableProperties<String, Level> {

	private static final Comparator<String> LEVEL_NAME_COMPARATOR = new Comparator<String>() {
		public int compare(String o1, String o2) {
			if (o1.equals(o2)) {
				return 0;
			}

			return o1.length() > o2.length() ? -1 : 1;
		};
	};

	private static final Function<Properties, Map<String, Level>> CONVERTER = (properties) -> {
		Map<String, Level> map = new HashMap<>();
		for (Entry<Object, Object> entry : properties.entrySet()) {
			Object key = entry.getKey();
			if (key == null) {
				continue;
			}

			Object value = entry.getValue();
			if (value == null) {
				continue;
			}

			Level level = CustomLevel.parse(value.toString());
			if (level == null) {
				continue;
			}

			map.put(String.valueOf(key), level);
		}
		return map;
	};

	public LevelManager() {
		super(new TreeMap<>(LEVEL_NAME_COMPARATOR), CONVERTER);
	}

	public boolean exists(String name) {
		if (containsKey(name)) {
			return true;
		}

		for (String key : keySet()) {
			if (name.startsWith(key)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 如果没有注册过会返回空
	 * 
	 * @param name
	 * @return
	 */
	@Nullable
	public Level getLevel(String name) {
		Level level = get(name);
		if (level != null) {
			return level;
		}

		for (Entry<String, Level> entry : entrySet()) {
			if (name.startsWith(entry.getKey())) {
				return entry.getValue();
			}
		}
		return null;
	}
}
