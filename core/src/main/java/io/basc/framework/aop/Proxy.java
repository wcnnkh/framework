package io.basc.framework.aop;

import io.basc.framework.beans.factory.InstanceCreator;

/**
 * 代理
 * 
 * @author wcnnkh
 *
 */
public interface Proxy extends InstanceCreator<Object, ProxyException> {

	Class<?> getSourceClass();
}
