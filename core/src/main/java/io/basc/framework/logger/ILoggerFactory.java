package io.basc.framework.logger;

/**
 * 扩展logger的一种方式,会使用spi机制加载
 * 
 * @author wcnnkh
 * @see LoggerFactory
 */
public interface ILoggerFactory {
	Logger getLogger(String name);
}
