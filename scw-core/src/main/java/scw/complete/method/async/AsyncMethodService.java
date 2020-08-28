package scw.complete.method.async;

import scw.beans.annotation.AopEnable;

@AopEnable(false)
public interface AsyncMethodService {
	void service(AsyncMethodCompleteTask asyncComplete) throws Exception;
}
