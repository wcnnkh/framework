package run.soeasy.framework.logging;

import java.util.logging.Level;

import lombok.NonNull;

public interface LevelFactory {
	/**
	 * 获取日志等级
	 * 
	 * @param name
	 * @return
	 */
	Level getLevel(@NonNull String name);
}
