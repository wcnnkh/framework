package io.basc.framework.logger;

import java.util.Map;
import java.util.logging.Level;

import io.basc.framework.event.EventListener;
import io.basc.framework.event.ObservableChangeEvent;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.Registration;

public abstract class CustomLogger implements Logger, EventListener<ObservableChangeEvent<Map<String, Level>>>, AutoCloseable {
	private Registration eventRegistration;
	private Level level;

	public synchronized void registerListener() {
		if (eventRegistration == null) {
			eventRegistration = LoggerFactory.getLevelManager().registerListener(this);
		}
	}

	@Override
	public void onEvent(ObservableChangeEvent<Map<String, Level>> event) {
		Level oldLevel = getLevel();
		Level newLevel = LoggerFactory.getLevelManager().getLevel(getName());
		if (!ObjectUtils.equals(oldLevel, newLevel)) {
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
		if (acceptLevel == null) {
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
