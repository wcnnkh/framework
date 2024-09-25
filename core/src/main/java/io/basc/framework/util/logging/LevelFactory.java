package io.basc.framework.util.logging;

import java.util.logging.Level;

public interface LevelFactory {
	/**
	 * 获取日志等级
	 * 
	 * @param name
	 * @return
	 */
	Level getLevel(String name);
}
