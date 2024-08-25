package io.basc.framework.util.logging;

/**
 * 扩展logger的一种方式,默认会使用spi机制加载
 * 
 * @author wcnnkh
 * @see LoggerFactory
 */
public interface ILoggerFactory {
	Logger getLogger(String name);
}
