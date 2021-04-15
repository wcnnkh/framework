package scw.logger;

import java.util.logging.Level;

public interface LevelFactory {
	Level getLevel(String name);
}
