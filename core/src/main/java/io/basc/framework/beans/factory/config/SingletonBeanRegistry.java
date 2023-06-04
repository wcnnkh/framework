package io.basc.framework.beans.factory.config;

import java.util.concurrent.locks.ReadWriteLock;

import io.basc.framework.beans.BeansException;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Elements;

public interface SingletonBeanRegistry {
	ReadWriteLock getReadWriteLock();

	void registerSingleton(String name, Object singletonObject) throws BeansException;

	void removeSingleton(String name) throws BeansException;

	@Nullable
	Object getSingleton(String name) throws BeansException;

	boolean containsSingleton(String name);

	/**
	 * 保持注册时的顺序
	 * 
	 * @return
	 */
	Elements<String> getRegistrationOrderSingletonNames();
}