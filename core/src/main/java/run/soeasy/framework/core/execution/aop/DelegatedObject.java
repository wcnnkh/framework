package run.soeasy.framework.core.execution.aop;

/**
 * 被代理的对象
 * 
 * @author wcnnkh
 *
 */
public interface DelegatedObject {
	public static final String PROXY_CONTAINER_ID_METHOD_NAME = "getProxyContainerId";

	/**
	 * 获取到aop容器的id
	 * 
	 * @return
	 */
	String getProxyContainerId();
}
