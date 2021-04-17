package scw.logger;

import java.util.logging.Level;

import scw.lang.Nullable;

public interface LevelFactory {
	/**
	 * 获取日志等级
	 * @param name
	 * @return
	 */
	@Nullable
	Level getLevel(String name);
}
