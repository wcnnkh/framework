package io.basc.framework.beans.factory.config;

public interface InitializingBean {
	void afterPropertiesSet() throws Exception;
}
