package scw.async.filter;

import scw.aop.Context;

public interface AsyncService {
	AsyncRunnableMethod create(Async async, Context context) throws Exception;

	void service(AsyncRunnableMethod asyncRunnableMethod) throws Exception;
}
