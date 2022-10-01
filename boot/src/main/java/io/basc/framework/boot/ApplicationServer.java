package io.basc.framework.boot;

/**
 * 一个application只会执行一次
 * 
 * @author wcnnkh
 *
 */
public interface ApplicationServer {
	void startup(ConfigurableApplication application) throws Throwable;
}