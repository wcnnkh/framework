package scw.complete.method.async;

import java.util.concurrent.Executor;

import scw.complete.Complete;
import scw.complete.CompleteService;
import scw.context.annotation.Provider;
import scw.transaction.DefaultTransactionLifecycle;
import scw.transaction.TransactionManager;
import scw.transaction.TransactionUtils;

@Provider
public class DefaultAsyncMethodService implements AsyncMethodService {
	private Executor executor;
	private CompleteService completeService;

	public DefaultAsyncMethodService(Executor executor, CompleteService completeService) {
		this.executor = executor;
		this.completeService = completeService;
	}

	public void service(AsyncMethodCompleteTask asyncComplete) throws Exception {
		final Complete complete = completeService.register(asyncComplete);
		TransactionManager manager = TransactionUtils.getManager();
		if (manager.hasTransaction()) {
			manager.getTransaction().addLifecycle(new DefaultTransactionLifecycle() {
				@Override
				public void afterRollback() {
					complete.cancel();
				}

				@Override
				public void afterCommit() {
					executor.execute(complete);
				}
			});
		} else {
			executor.execute(complete);
		}
	}
}
