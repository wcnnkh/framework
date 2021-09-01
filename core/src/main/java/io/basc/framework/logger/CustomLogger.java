package io.basc.framework.logger;

import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistration;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ObjectUtils;

import java.util.logging.Level;

public abstract class CustomLogger implements Logger, EventListener<ChangeEvent<LevelRegistry>>, AutoCloseable {
	private EventRegistration eventRegistration;
	private Level level;

	public synchronized void registerListener() {
		if(eventRegistration == null) {
			eventRegistration = LoggerFactory.getLevelManager().registerListener(this);
		}
	}

	@Override
	public void onEvent(ChangeEvent<LevelRegistry> event) {
		Level oldLevel = getLevel();
		Level newLevel = event.getSource().getLevel(getName());
		if (!ObjectUtils.nullSafeEquals(oldLevel, newLevel)) {
			info("Level [{}] change to [{}]", oldLevel, newLevel);
		}
		setLevel(newLevel);
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
		this.level = level;
	}
	
	@Override
	public void close() {
		if (eventRegistration != null) {
			eventRegistration.unregister();
		}
		eventRegistration = null;
	}
	
	@Override
	public String toString() {
		return "[" + getLevel() + "] " + getName();
	}
}
