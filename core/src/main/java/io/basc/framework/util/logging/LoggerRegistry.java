package io.basc.framework.util.logging;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;

import io.basc.framework.util.collections.Elements;
import lombok.NonNull;

public class LoggerRegistry extends LevelRegistry implements LoggerFactory {
	private Map<String, FacadeLogger> loggerMap = new TreeMap<String, FacadeLogger>();

	public FacadeLogger setLogger(String name, Logger logger) {
		synchronized (this) {
			FacadeLogger dynamicLogger = loggerMap.get(name);
			if (dynamicLogger == null) {
				dynamicLogger = new FacadeLogger(logger);
				Level level = getLevel(name);
				if (level != null) {
					dynamicLogger.setLevel(level);
				}

				loggerMap.put(name, dynamicLogger);
			} else {
				dynamicLogger.setSource(logger);
			}
			return dynamicLogger;
		}
	}

	public Elements<FacadeLogger> getLoggers() {
		return Elements.of(loggerMap.values());
	}

	@Override
	public void setLevel(@NonNull String name, Level level) {
		synchronized (this) {
			super.setLevel(name, level);
			for (Entry<String, FacadeLogger> entry : loggerMap.entrySet()) {
				if (match(name, entry.getKey())) {
					entry.getValue().setLevel(level);
				}
			}
		}
	}

	@Override
	public Logger getLogger(String name) {
		return loggerMap.get(name);
	}

}
