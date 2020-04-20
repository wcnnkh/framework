package scw.async.local;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import scw.beans.BeanFactory;

public class DefaultLocalAsyncExecutor extends ExecutorServiceAsyncExecutor {
	private final ExecutorService executorService = Executors
			.newScheduledThreadPool(4);

	public DefaultLocalAsyncExecutor(BeanFactory beanFactory) {
		super(true);
	}

	@Override
	protected ExecutorService getExecutorService() {
		return executorService;
	}
}
