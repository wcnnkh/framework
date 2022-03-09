package io.basc.framework.log4j2;

import java.util.logging.Level;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;

import io.basc.framework.logger.ILoggerFactory;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

public class Log4j2LoggerFactory implements ILoggerFactory {

	static {
		Log4j2Utils.reconfigure();
	}

	public Logger getLogger(String name) {
		org.apache.logging.log4j.Logger logger = LogManager.getLogger(name);
		Level level = LoggerFactory.getLevelManager().get().getLevel(name);
		if (level != null) {
			Configurator.setLevel(logger, LevelCodec.INSTANCE.encode(level));
		}
		return new Log4j2Logger(logger, null);
	}
}