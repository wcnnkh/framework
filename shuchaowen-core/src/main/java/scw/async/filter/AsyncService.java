package scw.async.filter;

import scw.aop.Context;

public interface AsyncService {
	void service(Async async, Context context) throws Exception;
}
