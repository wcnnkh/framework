package shuchaowen.core.http.rpc;

public interface Consumer {
	/**
	 * 获取服务
	 * @param interfaceClass
	 * @return
	 */
	public <T> T getService(Class<T> interfaceClass);
}
