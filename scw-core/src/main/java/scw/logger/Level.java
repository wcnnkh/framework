package scw.logger;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import scw.core.Assert;

public class Level implements Serializable {
	private static final long serialVersionUID = 1L;
	private volatile static Map<String, Level> levelNameMap = new HashMap<String, Level>();
	public static final Level ALL = new Level("ALL", Integer.MIN_VALUE);
	public static final Level TRACE = new Level("TRACE", 10000);
	public static final Level DEBUG = new Level("DEBUG", 20000);
	public static final Level INFO = new Level("INFO", 30000);
	public static final Level WARN = new Level("WARN", 40000);
	public static final Level ERROR = new Level("ERROR", 50000);
	public static final Level OFF = new Level("OFF", Integer.MAX_VALUE);

	private final String name;
	private final int value;

	public Level(String name, int value) {
		Assert.requiredArgument(name != null, "name");
		this.name = name.trim().toUpperCase();
		this.value = value;

		Level level = levelNameMap.get(name);
		if (level == null) {
			synchronized (levelNameMap) {
				level = levelNameMap.get(name);
				if (level == null) {
					levelNameMap.put(name, this);
					return;
				}
			}
		}
		throw new IllegalArgumentException("[" + name + "] already exists, use the forName method");
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof Level) {
			return name.equals(((Level) obj).name);
		}
		return false;
	}

	@Override
	public String toString() {
		return "name=" + name + ", value=" + value;
	}

	/**
	 * 
	 * 大于或等于
	 * @param level
	 * @return
	 */
	public boolean isGreaterOrEqual(Level level) {
		return value >= level.value;
	}

	public static Level getLevel(String name) {
		return levelNameMap.get(name.trim().toUpperCase());
	}

	public static Level getLevel(String name, int value) {
		Level level = getLevel(name);
		if (level == null) {
			try {
				level = new Level(name, value);
			} catch (IllegalArgumentException e) {
				return getLevel(name);
			}
		}
		return level;
	}
}
