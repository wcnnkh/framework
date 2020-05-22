package scw.complete.method.async;

import java.util.concurrent.Executor;

import scw.complete.Complete;
import scw.complete.CompleteService;

public abstract class AbstractAsyncMethodService implements AsyncMethodService {

	public abstract Executor getExecutor();

	public abstract CompleteService getCompleteService();

	public void service(AsyncMethodCompleteTask asyncComplete) throws Exception {
		Complete complete = getCompleteService().register(asyncComplete);
		getExecutor().execute(complete);
	}
}
