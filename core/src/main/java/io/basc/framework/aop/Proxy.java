package io.basc.framework.aop;

import io.basc.framework.factory.InstanceCreator;

/**
 * 代理
 * 
 * @author wcnnkh
 *
 */
public interface Proxy extends InstanceCreator<Object, ProxyException> {

	Class<?> getSourceClass();
}
