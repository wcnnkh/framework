package scw.slf4j;

import java.util.logging.Level;

import org.slf4j.Logger;

import scw.logger.AbstractLogger;
import scw.logger.CustomLevel;
import scw.util.PlaceholderFormatAppend;

/**
 *  并非支持所有的日志等级, 仅支持常规的info, debug, trace, warn, error
 * @author shuchaowen
 *
 */
public class Slf4jLogger extends AbstractLogger{
	private static final String FORMAT = "{}";
	
	private final Logger logger;
	
	public Slf4jLogger(Logger logger, String name, String placeholder) {
		super(name, placeholder);
		this.logger = logger;
	}

	@Override
	public String getName() {
		return logger.getName();
	}

	@Override
	public void log(Level level, Throwable e, Object format, Object... args) {
		PlaceholderFormatAppend message = new PlaceholderFormatAppend(format, placeholder, args);
		if(level.getName().equalsIgnoreCase(Level.INFO.getName())) {
			if(e == null) {
				logger.info(FORMAT, message);
			}else {
				logger.info(message.toString(), e);
			}
		}else if(level.getName().equalsIgnoreCase(CustomLevel.DEBUG.getName())){
			if(e == null) {
				logger.debug(FORMAT, message);
			}else {
				logger.debug(message.toString(), e);
			}
		}else if(level.getName().equalsIgnoreCase(CustomLevel.TRACE.getName())) {
			if(e == null) {
				logger.trace(FORMAT, message);
			}else {
				logger.trace(message.toString(), e);
			}
		}else if(level.getName().equalsIgnoreCase(CustomLevel.WARN.getName())) {
			if(e == null) {
				logger.warn(FORMAT, message);
			}else {
				logger.warn(message.toString(), e);
			}
		}else if(level.getName().equalsIgnoreCase(CustomLevel.ERROR.getName())) {
			if(e == null) {
				logger.error(FORMAT, message);
			}else {
				logger.error(message.toString(), e);
			}
		}else {
			if(e == null) {
				logger.info("Unsupported {} | {}", level, message);
			}else {
				logger.info("Unsupported " + level + " | " + message, e);
			}
		}
	}

}
