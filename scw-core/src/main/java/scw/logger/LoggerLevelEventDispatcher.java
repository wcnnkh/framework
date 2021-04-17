package scw.logger;

import scw.event.ChangeEvent;
import scw.event.EventType;
import scw.event.support.DefaultBasicEventDispatcher;

/**
 * 日志等级变更事件分发
 * 
 * @author shuchaowen
 *
 */
public final class LoggerLevelEventDispatcher extends DefaultBasicEventDispatcher<ChangeEvent<LevelFactory>> {
	private static LoggerLevelEventDispatcher instance = new LoggerLevelEventDispatcher();

	public static LoggerLevelEventDispatcher getInstance() {
		return instance;
	}

	private LoggerLevelEventDispatcher() {
		super(true);
	}

	public void publish(LevelFactory levelFactory) {
		publishEvent(new ChangeEvent<LevelFactory>(EventType.UPDATE, levelFactory));
	}
}
