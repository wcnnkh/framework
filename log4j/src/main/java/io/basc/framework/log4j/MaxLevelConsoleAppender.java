package io.basc.framework.log4j;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Priority;

/**
 * 限制最大等级的日志控制器
 * @author wcnnkh
 *
 */
public class MaxLevelConsoleAppender extends ConsoleAppender {
	private Priority maxPriority;

	public MaxLevelConsoleAppender(Priority maxPriority) {
		this.maxPriority = maxPriority;
	}

	@Override
	public boolean isAsSevereAsThreshold(Priority priority) {
		if (priority.isGreaterOrEqual(maxPriority)) {
			return false;
		}

		return super.isAsSevereAsThreshold(priority);
	}
}
