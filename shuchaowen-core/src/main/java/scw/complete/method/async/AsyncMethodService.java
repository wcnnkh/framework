package scw.complete.method.async;

import scw.beans.annotation.Bean;

@Bean(proxy=false)
public interface AsyncMethodService {
	void service(AsyncMethodCompleteTask asyncComplete) throws Exception;
}
