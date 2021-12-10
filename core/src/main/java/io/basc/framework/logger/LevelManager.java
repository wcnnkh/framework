package io.basc.framework.logger;

import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventListener;
import io.basc.framework.io.event.ConvertibleObservableProperties;

import java.util.Comparator;

/**
 * 动态管理日志等级管理<br/>
 * 
 * @author shuchaowen
 *
 */
public class LevelManager extends ConvertibleObservableProperties<LevelRegistry> {
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
		super(LevelRegistry.CONVERTER);
		customLevelRegistry.registerListener(new EventListener<ChangeEvent<LevelRegistry>>() {

			@Override
			public void onEvent(ChangeEvent<LevelRegistry> event) {
				LevelManager.this.publishEvent(new ChangeEvent<LevelRegistry>(event.getEventType(), forceGet()));
			}
		});
	}

	protected LevelRegistry forceGet() {
		LevelRegistry levelFactory = new LevelRegistry();
		levelFactory.putAll(super.forceGet());
		levelFactory.putAll(customLevelRegistry);
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
