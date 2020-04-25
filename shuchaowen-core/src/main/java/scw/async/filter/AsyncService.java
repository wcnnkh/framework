package scw.async.filter;

import scw.aop.ProxyContext;

public interface AsyncService {
	AsyncRunnableMethod create(Async async, ProxyContext context) throws Exception;

	void service(AsyncRunnableMethod asyncRunnableMethod) throws Exception;
}
