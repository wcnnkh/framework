package run.soeasy.framework.logging;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * 包装的jdk日志记录器
 * 
 * @author soeasy.run
 *
 */
public class JdkLogger extends AbstractLogger {
	private final Logger logger;

	public JdkLogger(Logger logger) {
		this.logger = logger;
	}

	@Override
	public String getName() {
		return logger.getName();
	}

	public Logger getTargetLogger() {
		return logger;
	}

	@Override
	public boolean isLoggable(Level level) {
		return super.isLoggable(level) && logger.isLoggable(level);
	}

	@Override
	public void log(LogRecord record) {
		String message = FormatableMessage.formatPlaceholder(record.getMessage(), null, record.getParameters());
		record.setMessage(message);
		record.setParameters(null);
		logger.log(record);
	}

	@Override
	public void setLevel(Level level) {
		super.setLevel(level);
		logger.setLevel(level);
		Logger parent = this.logger;
		while (parent != null) {
			for (Handler handler : parent.getHandlers()) {
				handler.setLevel(level);
			}

			if (parent.getUseParentHandlers()) {
				parent = parent.getParent();
			} else {
				break;
			}
		}
	}
}
