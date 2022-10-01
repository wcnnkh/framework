package io.basc.framework.logger;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.logging.Level;

import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventDispatcher;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistration;
import io.basc.framework.event.EventType;
import io.basc.framework.event.support.SimpleEventDispatcher;
import io.basc.framework.lang.Nullable;

public class LevelRegistry extends TreeMap<String, Level> implements EventDispatcher<ChangeEvent<LevelRegistry>> {
	private static final long serialVersionUID = 1L;

	public static final Comparator<String> LEVEL_NAME_COMPARATOR = new Comparator<String>() {
		public int compare(String o1, String o2) {
			if (o1.equals(o2)) {
				return 0;
			}

			return o1.length() > o2.length() ? -1 : 1;
		};
	};

	public static final Function<Properties, LevelRegistry> CONVERTER = new Function<Properties, LevelRegistry>() {

		@Override
		public LevelRegistry apply(Properties properties) {
			LevelRegistry levelFactory = new LevelRegistry();
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

				levelFactory.put(String.valueOf(key), level);
			}
			return levelFactory;
		}
	};

	private final EventDispatcher<ChangeEvent<LevelRegistry>> dispatcher = new SimpleEventDispatcher<ChangeEvent<LevelRegistry>>();

	@Override
	public EventRegistration registerListener(EventListener<ChangeEvent<LevelRegistry>> eventListener) {
		return dispatcher.registerListener(eventListener);
	}

	@Override
	public void publishEvent(ChangeEvent<LevelRegistry> event) {
		dispatcher.publishEvent(event);
	}

	public LevelRegistry() {
		super(LEVEL_NAME_COMPARATOR);
	}

	private LevelRegistry(SortedMap<String, Level> levelMap) {
		super(levelMap);
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

	@Override
	public LevelRegistry clone() {
		return new LevelRegistry(this);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Level> map) {
		try {
			super.putAll(map);
		} finally {
			if (dispatcher != null) {
				dispatcher.publishEvent(new ChangeEvent<LevelRegistry>(EventType.UPDATE, this));
			}
		}
	}

	@Override
	public Level put(String key, Level value) {
		synchronized (this) {
			try {
				return super.put(key, value);
			} finally {
				dispatcher.publishEvent(new ChangeEvent<LevelRegistry>(EventType.UPDATE, this));
			}
		}
	}

	@Override
	public Level remove(Object key) {
		synchronized (this) {
			try {
				return super.remove(key);
			} finally {
				dispatcher.publishEvent(new ChangeEvent<LevelRegistry>(EventType.UPDATE, this));
			}
		}
	}

	@Override
	public void clear() {
		synchronized (this) {
			try {
				super.clear();
			} finally {
				dispatcher.publishEvent(new ChangeEvent<LevelRegistry>(EventType.UPDATE, this));
			}
		}
	}
}
