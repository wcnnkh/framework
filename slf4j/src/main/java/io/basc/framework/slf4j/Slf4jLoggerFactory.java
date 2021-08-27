package io.basc.framework.slf4j;

import io.basc.framework.logger.ILoggerFactory;
import io.basc.framework.logger.Logger;

import org.slf4j.LoggerFactory;

public class Slf4jLoggerFactory implements ILoggerFactory {

	@Override
	public Logger getLogger(String name) {
		org.slf4j.Logger logger = LoggerFactory.getLogger(name);
		return new Slf4jLogger(logger, null);
	}
}
