package scw.logger.log4j2;

import org.apache.logging.log4j.Logger;

import scw.logger.AbstractLogger;
import scw.logger.Level;

public class Log4j2Logger extends AbstractLogger {
	private final Logger logger;

	public Log4j2Logger(Logger logger, Level level, String placeholder) {
		super(level, placeholder);
		this.logger = logger;
	}

	public Logger getLogger() {
		return logger;
	}

	public String getName() {
		return logger.getName();
	}

	public boolean isInfoEnabled() {
		return super.isInfoEnabled() && logger.isInfoEnabled();
	}

	public void info(Object format, Object... args) {
		if(!isInfoEnabled()){
			return ;
		}
		
		logger.info(createMessage(format, args));
	}

	public void info(Throwable e, Object format, Object... args) {
		if(!isInfoEnabled()){
			return ;
		}
		
		logger.info(createMessage(format, args), e);
	}

	public boolean isTraceEnabled() {
		return super.isTraceEnabled() && logger.isTraceEnabled();
	}

	public void trace(Object format, Object... args) {
		if(!isTraceEnabled()){
			return ;
		}
		
		logger.trace(createMessage(format, args));
	}

	public void trace(Throwable e, Object format, Object... args) {
		if(!isTraceEnabled()){
			return ;
		}
		
		logger.trace(createMessage(format, args), e);
	}

	@Override
	public boolean isWarnEnabled() {
		return super.isWarnEnabled() && logger.isWarnEnabled();
	}

	public void warn(Object format, Object... args) {
		if(!isWarnEnabled()){
			return ;
		}
		
		logger.warn(createMessage(format, args));
	}

	public void warn(Throwable e, Object format, Object... args) {
		if(!isWarnEnabled()){
			return ;
		}
		
		logger.warn(createMessage(format, args), e);
	}

	@Override
	public boolean isErrorEnabled() {
		return super.isErrorEnabled() && logger.isErrorEnabled();
	}

	public void error(Object format, Object... args) {
		if(!isErrorEnabled()){
			return ;
		}
		
		logger.error(createMessage(format, args));
	}

	public void error(Throwable e, Object format, Object... args) {
		if(!isErrorEnabled()){
			return ;
		}
		
		logger.error(createMessage(format, args), e);
	}

	public boolean isDebugEnabled() {
		return super.isDebugEnabled() && logger.isDebugEnabled();
	}

	public void debug(Object format, Object... args) {
		if(!isDebugEnabled()){
			return ;
		}
		
		logger.debug(createMessage(format, args));
	}

	public void debug(Throwable e, Object format, Object... args) {
		if(!isDebugEnabled()){
			return ;
		}
		
		logger.debug(createMessage(format, args), e);
	}
}
