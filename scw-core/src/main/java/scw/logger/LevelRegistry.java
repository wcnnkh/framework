package scw.logger;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;

import scw.event.BasicEventDispatcher;
import scw.event.ChangeEvent;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.EventType;
import scw.event.support.DefaultBasicEventDispatcher;
import scw.lang.Nullable;

public class LevelRegistry extends TreeMap<String, Level> implements
		BasicEventDispatcher<ChangeEvent<LevelRegistry>> {
	private static final long serialVersionUID = 1L;

	public static final Comparator<String> LEVEL_NAME_COMPARATOR = new Comparator<String>() {
		public int compare(String o1, String o2) {
			if (o1.equals(o2)) {
				return 0;
			}

			return o1.length() > o2.length() ? -1 : 1;
		};
	};

	private volatile BasicEventDispatcher<ChangeEvent<LevelRegistry>> dispatcher;

	@Override
	public EventRegistration registerListener(
			EventListener<ChangeEvent<LevelRegistry>> eventListener) {
		if (dispatcher == null) {
			synchronized (this) {
				if (dispatcher == null) {
					dispatcher = new DefaultBasicEventDispatcher<ChangeEvent<LevelRegistry>>(
							true);
				}
			}
		}
		return dispatcher.registerListener(eventListener);
	}

	@Override
	public void publishEvent(ChangeEvent<LevelRegistry> event) {
		if (dispatcher == null) {
			return;
		}
		dispatcher.publishEvent(event);
	}

	@Nullable
	public final BasicEventDispatcher<ChangeEvent<LevelRegistry>> getDispatcher() {
		return dispatcher;
	}

	public void setDispatcher(
			BasicEventDispatcher<ChangeEvent<LevelRegistry>> dispatcher) {
		this.dispatcher = dispatcher;
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
				dispatcher.publishEvent(new ChangeEvent<LevelRegistry>(
						EventType.UPDATE, this));
			}
		}
	}

	@Override
	public Level put(String key, Level value) {
		synchronized (this) {
			try {
				return super.put(key, value);
			} finally {
				if (dispatcher != null) {
					dispatcher.publishEvent(new ChangeEvent<LevelRegistry>(
							EventType.UPDATE, this));
				}
			}
		}
	}

	@Override
	public Level remove(Object key) {
		synchronized (this) {
			try {
				return super.remove(key);
			} finally {
				if (dispatcher != null) {
					dispatcher.publishEvent(new ChangeEvent<LevelRegistry>(
							EventType.UPDATE, this));
				}
			}
		}
	}

	@Override
	public void clear() {
		synchronized (this) {
			try {
				super.clear();
			} finally {
				if (dispatcher != null) {
					dispatcher.publishEvent(new ChangeEvent<LevelRegistry>(
							EventType.UPDATE, this));
				}
			}
		}
	}
}
