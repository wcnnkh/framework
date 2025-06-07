package run.soeasy.framework.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import lombok.NonNull;
import run.soeasy.framework.core.domain.Wrapper;

public class FacadeLogger extends AbstractLogger implements Wrapper<Logger> {
	private volatile Logger source;

	public FacadeLogger(@NonNull Logger source) {
		this.source = source;
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
	public void setLevel(Level level) {
		super.setLevel(level);
		setLevel(source, level);
	}

	private void setLevel(Logger logger, Level level) {
		if (logger instanceof AbstractLogger) {
			try {
				((AbstractLogger) logger).setLevel(level);
			} catch (Throwable e) {
				error(e, "Exception in setting the source logger log level");
			}
		}
	}

	public void setSource(@NonNull Logger source) {
		Logger oldLogger = this.source;
		this.source = source;
		info("Logger change from {} to {}", oldLogger, this.source);
	}
}
