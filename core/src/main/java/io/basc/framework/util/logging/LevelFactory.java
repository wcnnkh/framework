package io.basc.framework.util.logging;

import java.util.logging.Level;

public interface LevelFactory {
	Level getLevel(String name);
}
