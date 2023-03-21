package io.basc.framework.logger;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import io.basc.framework.event.EventListener;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ObjectUtils;

public class DynamicLogger implements Logger, EventListener<LevelManager> {
	private volatile Level level;
	private volatile Logger source;

	@Override
	public Level getLevel() {
		Level level = this.level;
		if (level == null) {
			level = source.getLevel();
		}
		return level;
	}

	@Override
	public String getName() {
		return source.getName();
	}

	public Logger getSource() {
		return source;
	}

	@Override
	public void log(LogRecord record) {
		if (isLoggable(record.getLevel())) {
			source.log(record);
		}
	}

	@Override
	public void onEvent(LevelManager event) {
		Level oldLevel = getLevel();
		Level newLevel = event.getLevel(getName());
		if (!ObjectUtils.equals(oldLevel, newLevel)) {
			info("Log level changed from {} to {}", oldLevel, newLevel);
			setLevel(newLevel);
		}
	}

	@Override
	public void setLevel(Level level) {
		this.level = level;
		try {
			source.setLevel(level);
		} catch (Throwable e) {
			error(e, "Exception in setting the source logger log level");
		}
	}

	public void setSource(Logger source) {
		Assert.requiredArgument(source != null, "source");
		this.source = source;
	}
}
