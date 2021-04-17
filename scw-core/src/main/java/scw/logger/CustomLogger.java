package scw.logger;

import java.util.logging.Level;

import scw.core.utils.ObjectUtils;
import scw.event.ChangeEvent;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.lang.Nullable;

public abstract class CustomLogger implements Logger, EventListener<ChangeEvent<LevelFactory>> {
	private EventRegistration eventRegistration;
	private Level level;

	public synchronized void registerListener() {
		if(eventRegistration == null) {
			eventRegistration = LoggerLevelEventDispatcher.getInstance().registerListener(this);
		}
	}

	@Override
	public void onEvent(ChangeEvent<LevelFactory> event) {
		setLevel(event.getSource().getLevel(getName()));
	}

	@Nullable
	public Level getLevel() {
		return level;
	}
	
	@Override
	public boolean isLoggable(Level level) {
		Level acceptLevel = getLevel();
		if(acceptLevel == null) {
			return true;
		}
		
		return CustomLevel.isGreaterOrEqual(level, acceptLevel);
	}

	public void setLevel(@Nullable Level level) {
		if (ObjectUtils.nullSafeEquals(getLevel(), level)) {
			// 这里使用off是为了任意日志级别都会显示该日志
			log(Level.OFF, "Level [{}] change to [{}]", getLevel(), level);
		}
		this.level = level;
	}
	
	@Override
	protected void finalize() throws Throwable {
		if (eventRegistration != null) {
			eventRegistration.unregister();
		}
		eventRegistration = null;
		super.finalize();
	}
}
