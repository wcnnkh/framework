package io.basc.framework.execution.aop;

/**
 * 被代理的对象
 * 
 * @author wcnnkh
 *
 */
public interface DelegatedObject {
	/**
	 * 获取到aop容器的id
	 * 
	 * @return
	 */
	String getProxyContainerId();
}
