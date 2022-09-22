package io.basc.framework.aop;

import io.basc.framework.factory.InstanceCreator;

/**
 * 代理
 * 
 * @author shuchaowen
 *
 */
public interface Proxy extends InstanceCreator<Object, ProxyException> {
	/**
	 * 被代理的原始类
	 * 
	 * @return
	 */
	Class<?> getSourceClass();
}
