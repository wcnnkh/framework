package scw.complete.method.async;

import java.util.concurrent.Executor;

import scw.complete.Complete;
import scw.complete.CompleteService;
import scw.core.instance.annotation.SPI;
import scw.transaction.DefaultTransactionLifeCycle;
import scw.transaction.TransactionManager;

@SPI(order = Integer.MIN_VALUE)
public class DefaultAsyncMethodService implements AsyncMethodService {
	private Executor executor;
	private CompleteService completeService;

	public DefaultAsyncMethodService(Executor executor, CompleteService completeService) {
		this.executor = executor;
		this.completeService = completeService;
	}

	public void service(AsyncMethodCompleteTask asyncComplete) throws Exception {
		final Complete complete = completeService.register(asyncComplete);
		if (TransactionManager.hasTransaction()) {
			TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
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
