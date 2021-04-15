package scw.slf4j;

import org.slf4j.LoggerFactory;

import scw.logger.ILoggerFactory;
import scw.logger.Logger;

public class Slf4jLoggerFactory implements ILoggerFactory {

	@Override
	public Logger getLogger(String name) {
		org.slf4j.Logger logger = LoggerFactory.getLogger(name);
		return new Slf4jLogger(logger, null);
	}
}
