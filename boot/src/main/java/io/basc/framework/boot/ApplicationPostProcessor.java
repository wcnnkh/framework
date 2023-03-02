package io.basc.framework.boot;

/**
 * 会在应用初始化成功后全局调用此类的方法
 * 
 * @author wcnnkh
 *
 */
@FunctionalInterface
public interface ApplicationPostProcessor {
	void postProcessApplication(ConfigurableApplication application) throws Throwable;
}
