package scw.logger;

import java.util.logging.Level;

import scw.core.utils.ObjectUtils;
import scw.event.ChangeEvent;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.lang.Nullable;

public abstract class CustomLogger implements Logger, EventListener<ChangeEvent<LevelRegistry>> {
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
	protected void finalize() throws Throwable {
		if (eventRegistration != null) {
			eventRegistration.unregister();
		}
		eventRegistration = null;
		super.finalize();
	}
	
	@Override
	public String toString() {
		return "[" + getLevel() + "] " + getName();
	}
}
