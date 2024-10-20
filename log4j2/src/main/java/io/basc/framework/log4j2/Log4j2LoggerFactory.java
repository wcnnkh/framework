package io.basc.framework.log4j2;

import org.apache.logging.log4j.LogManager;

import io.basc.framework.util.logging.LoggerFactory;
import io.basc.framework.util.logging.Logger;

public class Log4j2LoggerFactory implements LoggerFactory {

	static {
		Log4j2Utils.reconfigure();
	}

	public Logger getLogger(String name) {
		org.apache.logging.log4j.Logger logger = LogManager.getLogger(name);
		return new Log4j2Logger(logger, null);
	}
}