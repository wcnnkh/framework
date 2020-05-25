package scw.complete.method.async;

import java.util.concurrent.Executor;

import scw.complete.Complete;
import scw.complete.CompleteService;

public abstract class AbstractAsyncMethodService implements AsyncMethodService {
	private Executor executor;

	public AbstractAsyncMethodService(Executor executor) {
		this.executor = executor;
	}

	public abstract CompleteService getCompleteService();

	public void service(AsyncMethodCompleteTask asyncComplete) throws Exception {
		Complete complete = getCompleteService().register(asyncComplete);
		executor.execute(complete);
	}
}
