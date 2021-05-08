package scw.logger;

import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;

import scw.event.ChangeEvent;
import scw.event.EventListener;
import scw.io.event.ConvertibleObservablesProperties;

/**
 * 动态管理日志等级管理<br/>
 * @author shuchaowen
 *
 */
public class LevelManager extends ConvertibleObservablesProperties<LevelRegistry>{
	public static final Comparator<String> LEVEL_NAME_COMPARATOR = new Comparator<String>() {
		public int compare(String o1, String o2) {
			if (o1.equals(o2)) {
				return 0;
			}

			return o1.length() > o2.length() ? -1 : 1;
		};
	};
	
	private final LevelRegistry customLevelRegistry = new LevelRegistry();

	public LevelManager() {
		super(true);
		customLevelRegistry.registerListener(new EventListener<ChangeEvent<LevelRegistry>>() {
			
			@Override
			public void onEvent(ChangeEvent<LevelRegistry> event) {
				onEvent(new ChangeEvent<LevelRegistry>(event.getEventType(), forceGet()));
			}
		});
	}
	
	@Override
	public LevelRegistry forceGet() {
		LevelRegistry levelFactory = new LevelRegistry();
		levelFactory.putAll(super.forceGet());
		levelFactory.putAll(customLevelRegistry);
		return levelFactory;
	}

	public LevelRegistry convert(Properties properties) {
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

	@Override
	public LevelRegistry get() {
		return super.get().clone();
	}

	public LevelRegistry getCustomLevelRegistry() {
		return customLevelRegistry;
	}
}
